import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, Observable, shareReplay, switchMap, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import type { AuthResponse } from '../models/api.models';

/** Compartilha um único refresh em andamento entre requisições concorrentes. */
let refreshInFlight: Observable<AuthResponse> | null = null;

function withBearer(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}

/**
 * Anexa o access token às requisições e, em caso de 401, tenta renovar a
 * sessão com o refresh token uma única vez antes de deslogar o usuário.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const tokenStorage = inject(TokenStorageService);

  const isAuthEndpoint = req.url.startsWith('/auth/');
  const isExternal = req.url.startsWith('http');
  const accessToken = tokenStorage.getAccessToken();

  const outgoing =
    accessToken && !isAuthEndpoint && !isExternal ? withBearer(req, accessToken) : req;

  return next(outgoing).pipe(
    catchError((error: unknown) => {
      const is401 = error instanceof HttpErrorResponse && error.status === 401;
      if (!is401 || isAuthEndpoint || isExternal || !auth.hasRefreshToken()) {
        return throwError(() => error);
      }

      refreshInFlight ??= auth.refresh().pipe(
        finalize(() => (refreshInFlight = null)),
        shareReplay(1),
      );

      return refreshInFlight.pipe(
        switchMap((tokens) => next(withBearer(req, tokens.accessToken))),
        catchError((refreshError: unknown) => {
          auth.logout();
          return throwError(() => refreshError);
        }),
      );
    }),
  );
};

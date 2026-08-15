import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { TokenStorageService } from './token-storage.service';
import type {
  AuthResponse,
  ConfirmRegisterRequest,
  ForgotPasswordRequest,
  LoginRequest,
  MessageResponse,
  RegisterRequest,
  ResetPasswordRequest,
} from '../models/api.models';

interface JwtPayload {
  sub?: string;
  exp?: number;
}

function decodeJwtPayload(token: string): JwtPayload {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(decodeURIComponent(escape(atob(base64)))) as JwtPayload;
  } catch {
    return {};
  }
}

/** Autenticação: login, registro em 2 etapas, recuperação de senha e sessão. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenStorage = inject(TokenStorageService);

  private readonly accessToken = signal<string | null>(this.tokenStorage.getAccessToken());

  readonly isAuthenticated = computed(() => {
    const token = this.accessToken();
    if (!token) {
      return false;
    }
    const { exp } = decodeJwtPayload(token);
    return exp === undefined || exp * 1000 > Date.now();
  });

  /** E-mail do usuário logado (claim `sub` do access token). */
  readonly username = computed(() => {
    const token = this.accessToken();
    return token ? (decodeJwtPayload(token).sub ?? null) : null;
  });

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/auth/login', request)
      .pipe(tap((tokens) => this.storeSession(tokens)));
  }

  register(request: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>('/auth/register', request);
  }

  confirmRegister(request: ConfirmRegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/auth/register/confirm', request)
      .pipe(tap((tokens) => this.storeSession(tokens)));
  }

  forgotPassword(request: ForgotPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>('/auth/forgot-password', request);
  }

  resetPassword(request: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>('/auth/reset-password', request);
  }

  refresh(): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/auth/refresh', { refreshToken: this.tokenStorage.getRefreshToken() })
      .pipe(tap((tokens) => this.storeSession(tokens)));
  }

  hasRefreshToken(): boolean {
    return this.tokenStorage.getRefreshToken() !== null;
  }

  logout(redirect = true): void {
    this.tokenStorage.clear();
    this.accessToken.set(null);
    if (redirect) {
      void this.router.navigate(['/login']);
    }
  }

  private storeSession(tokens: AuthResponse): void {
    this.tokenStorage.save(tokens.accessToken, tokens.refreshToken);
    this.accessToken.set(tokens.accessToken);
  }
}

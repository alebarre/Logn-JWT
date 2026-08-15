import { inject, Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';

import type { ApiError } from '../models/api.models';

/**
 * Toasts padronizados da aplicação (avisos e respostas ao usuário).
 * Diálogos de confirmação ficam a cargo do ConfirmationService/p-confirmdialog.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly messageService = inject(MessageService);

  success(detail: string, summary = 'Sucesso'): void {
    this.messageService.add({ severity: 'success', summary, detail, life: 4000 });
  }

  info(detail: string, summary = 'Informação'): void {
    this.messageService.add({ severity: 'info', summary, detail, life: 4000 });
  }

  warn(detail: string, summary = 'Atenção'): void {
    this.messageService.add({ severity: 'warn', summary, detail, life: 5000 });
  }

  error(detail: string, summary = 'Erro'): void {
    this.messageService.add({ severity: 'error', summary, detail, life: 6000 });
  }

  /** Exibe a mensagem de erro vinda da API (ApiErrorDTO) ou um fallback amigável. */
  apiError(error: unknown, fallback = 'Algo deu errado. Tente novamente.'): void {
    this.error(extractApiErrorMessage(error) ?? fallback);
  }
}

export function extractApiErrorMessage(error: unknown): string | null {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    const body = error.error as Partial<ApiError> | null;
    if (body?.message) {
      return body.message;
    }
  }
  return null;
}

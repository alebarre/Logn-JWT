import { HttpErrorResponse } from '@angular/common/http';
import type { AbstractControl, FormGroup } from '@angular/forms';

import type { ApiError } from '../models/api.models';

/**
 * Aplica os erros de validação do backend (ApiErrorDTO.errors, no formato
 * "campo" ou "enderecos[0].cep") aos controles correspondentes do formulário.
 * Retorna true se ao menos um erro foi vinculado a um controle.
 */
export function applyServerValidationErrors(form: FormGroup, error: unknown): boolean {
  if (!(error instanceof HttpErrorResponse)) {
    return false;
  }
  const body = error.error as Partial<ApiError> | null;
  if (!body?.errors) {
    return false;
  }

  let applied = false;
  for (const [field, message] of Object.entries(body.errors)) {
    const control = resolveControl(form, field);
    if (control) {
      control.setErrors({ ...control.errors, server: message });
      control.markAsTouched();
      applied = true;
    }
  }
  return applied;
}

/** Resolve caminhos como "enderecos[0].cep" dentro do FormGroup. */
function resolveControl(form: FormGroup, path: string): AbstractControl | null {
  const normalized = path.replace(/\[(\d+)\]/g, '.$1');
  return form.get(normalized);
}

/** Mensagem de erro amigável para um controle de formulário. */
export function controlErrorMessage(control: AbstractControl | null): string | null {
  if (!control || !control.touched || !control.errors) {
    return null;
  }
  const errors = control.errors;
  if (errors['server']) {
    return errors['server'] as string;
  }
  if (errors['required']) {
    return 'Campo obrigatório';
  }
  if (errors['email']) {
    return 'E-mail inválido';
  }
  if (errors['minlength']) {
    return `Mínimo de ${errors['minlength'].requiredLength} caracteres`;
  }
  if (errors['maxlength']) {
    return `Máximo de ${errors['maxlength'].requiredLength} caracteres`;
  }
  if (errors['pattern']) {
    return 'Formato inválido';
  }
  if (errors['min']) {
    return `Valor mínimo: ${errors['min'].min}`;
  }
  return 'Valor inválido';
}

import { ChangeDetectionStrategy, Component, inject, signal, viewChild } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';

import { AuthCard } from '../shared/auth-card';
import { CodeVerification } from '../shared/code-verification';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { applyServerValidationErrors, controlErrorMessage } from '../../../core/utils/form-errors';
import { APP_CONFIG } from '../../../core/config/app-config';

/** Valida que a confirmação de senha é igual à nova senha. */
function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const password = group.get('newPassword')?.value;
  const confirmation = group.get('confirmPassword')?.value;
  return password && confirmation && password !== confirmation ? { passwordsMismatch: true } : null;
}

@Component({
  selector: 'app-forgot',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    AuthCard,
    CodeVerification,
  ],
  templateUrl: './forgot.html',
})
export class Forgot {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notification = inject(NotificationService);

  protected readonly step = signal<'email' | 'reset'>('email');
  protected readonly loading = signal(false);
  protected readonly codeInput = viewChild(CodeVerification);

  protected readonly emailForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  protected readonly resetForm = this.fb.group(
    {
      newPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      confirmPassword: ['', Validators.required],
    },
    { validators: passwordsMatch },
  );

  protected get email(): string {
    return this.emailForm.getRawValue().email;
  }

  protected emailError(): string | null {
    return controlErrorMessage(this.emailForm.get('email'));
  }

  protected resetError(name: string): string | null {
    const control = this.resetForm.get(name);
    const message = controlErrorMessage(control);
    if (message) {
      return message;
    }
    if (name === 'confirmPassword' && control?.touched && this.resetForm.errors?.['passwordsMismatch']) {
      return 'As senhas não coincidem';
    }
    return null;
  }

  /** Passo 1: solicita o envio do código de recuperação por e-mail. */
  protected requestCode(): void {
    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.auth
      .forgotPassword(this.emailForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.notification.info(response.message);
          this.step.set('reset');
        },
        error: (error: unknown) => {
          if (!applyServerValidationErrors(this.emailForm, error)) {
            this.notification.apiError(error, 'Não foi possível enviar o código de recuperação.');
          }
        },
      });
  }

  /** Passo 2: confirma o código e redefine a senha. */
  protected resetPassword(): void {
    const code = this.codeInput()?.code() ?? '';
    if (code.length !== APP_CONFIG.verificationCodeLength) {
      this.notification.warn('Informe o código de 5 dígitos enviado para o seu e-mail.');
      return;
    }
    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.auth
      .resetPassword({ email: this.email, code, newPassword: this.resetForm.getRawValue().newPassword })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.notification.success(response.message);
          void this.router.navigate(['/login']);
        },
        error: (error: unknown) => {
          if (!applyServerValidationErrors(this.resetForm, error)) {
            this.notification.apiError(error, 'Código inválido ou expirado.');
          }
        },
      });
  }

  protected resendCode(): void {
    this.loading.set(true);
    this.auth
      .forgotPassword(this.emailForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.notification.info(response.message);
          this.codeInput()?.restart();
        },
        error: (error: unknown) => this.notification.apiError(error, 'Não foi possível reenviar o código.'),
      });
  }
}

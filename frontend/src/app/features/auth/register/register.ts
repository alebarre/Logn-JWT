import { ChangeDetectionStrategy, Component, inject, signal, viewChild } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
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

@Component({
  selector: 'app-register',
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
  templateUrl: './register.html',
})
export class Register {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notification = inject(NotificationService);

  protected readonly step = signal<'form' | 'code'>('form');
  protected readonly loading = signal(false);
  protected readonly codeInput = viewChild(CodeVerification);

  protected readonly form = this.fb.group({
    username: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
  });

  protected get email(): string {
    return this.form.getRawValue().username;
  }

  protected errorOf(name: string): string | null {
    return controlErrorMessage(this.form.get(name));
  }

  /** Etapa 1: envia os dados e dispara o código de confirmação por e-mail. */
  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.auth
      .register(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.notification.info(response.message);
          this.step.set('code');
        },
        error: (error: unknown) => {
          if (!applyServerValidationErrors(this.form, error)) {
            this.notification.apiError(error, 'Não foi possível iniciar o cadastro.');
          }
        },
      });
  }

  /** Etapa 2: confirma o código e entra direto na aplicação. */
  protected confirm(): void {
    const code = this.codeInput()?.code() ?? '';
    if (code.length !== APP_CONFIG.verificationCodeLength) {
      this.notification.warn('Informe o código de 5 dígitos enviado para o seu e-mail.');
      return;
    }
    this.loading.set(true);
    this.auth
      .confirmRegister({ email: this.email, code })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.notification.success('Conta criada com sucesso. Bem-vindo(a)!');
          void this.router.navigateByUrl('/');
        },
        error: (error: unknown) => this.notification.apiError(error, 'Código inválido ou expirado.'),
      });
  }

  /** Reenvia o código (um novo registro substitui o cadastro pendente anterior). */
  protected resendCode(): void {
    this.loading.set(true);
    this.auth
      .register(this.form.getRawValue())
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

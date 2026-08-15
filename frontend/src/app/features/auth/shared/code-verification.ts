import {
  ChangeDetectionStrategy,
  Component,
  input,
  model,
  OnDestroy,
  OnInit,
  output,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InputOtpModule } from 'primeng/inputotp';
import { ButtonModule } from 'primeng/button';

import { APP_CONFIG } from '../../../core/config/app-config';

/**
 * Entrada do código de 5 dígitos enviado por e-mail, com contagem regressiva
 * de validade e reenvio. O reenvio é delegado ao componente pai via output.
 */
@Component({
  selector: 'app-code-verification',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, InputOtpModule, ButtonModule],
  template: `
    <div class="code-verification">
      <p class="code-hint">
        Enviamos um código de {{ codeLength }} dígitos para <strong>{{ email() }}</strong>
      </p>

      <p-inputotp
        [ngModel]="code()"
        (ngModelChange)="code.set($event)"
        [length]="codeLength"
        [integerOnly]="true"
        [disabled]="disabled()"
        [style]="{ justifyContent: 'center', gap: '0.5rem' }"
      />

      <p class="code-status" aria-live="polite">
        @if (secondsLeft() > 0) {
          O código expira em <strong>{{ secondsLeft() }}s</strong>
        } @else {
          Código expirado — reenvie para receber um novo.
        }
      </p>

      <p-button
        label="Reenviar código"
        link
        size="small"
        [disabled]="secondsLeft() > 0 || disabled()"
        (onClick)="resend.emit()"
      />
    </div>
  `,
  styles: `
    .code-verification {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.9rem;
      text-align: center;
    }

    .code-hint {
      margin: 0;
      color: var(--app-text-muted);
      font-size: 0.9rem;
      overflow-wrap: anywhere;
    }

    .code-status {
      margin: 0;
      font-size: 0.85rem;
      color: var(--app-text-muted);
    }
  `,
})
export class CodeVerification implements OnInit, OnDestroy {
  readonly email = input.required<string>();
  readonly disabled = input(false);
  readonly code = model('');
  readonly resend = output<void>();

  protected readonly codeLength = APP_CONFIG.verificationCodeLength;
  protected readonly secondsLeft = signal(0);

  private timer?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.restart();
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }

  /** Reinicia a contagem regressiva (chamado pelo pai após reenviar o código). */
  restart(): void {
    clearInterval(this.timer);
    this.code.set('');
    this.secondsLeft.set(APP_CONFIG.verificationCodeTtlSeconds);
    this.timer = setInterval(() => {
      this.secondsLeft.update((s) => {
        if (s <= 1) {
          clearInterval(this.timer);
          return 0;
        }
        return s - 1;
      });
    }, 1000);
  }
}

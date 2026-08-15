import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faShieldHalved } from '@fortawesome/free-solid-svg-icons';

/** Cartão centralizado usado por todas as telas de autenticação. */
@Component({
  selector: 'app-auth-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FontAwesomeModule],
  template: `
    <div class="auth-page">
      <main class="auth-card" role="main">
        <header class="auth-brand">
          <span class="auth-brand-icon" aria-hidden="true">
            <fa-icon [icon]="faShieldHalved" />
          </span>
          <h1>{{ title() }}</h1>
          @if (subtitle()) {
            <p>{{ subtitle() }}</p>
          }
        </header>
        <ng-content />
      </main>
    </div>
  `,
  styles: `
    .auth-page {
      min-height: 100dvh;
      display: grid;
      place-items: center;
      padding: 1.25rem;
      background:
        radial-gradient(60rem 30rem at 110% -10%, #d3ede2 0%, transparent 60%),
        radial-gradient(50rem 26rem at -10% 110%, #dbe7f6 0%, transparent 55%),
        var(--app-bg);
    }

    .auth-card {
      width: min(26.5rem, 100%);
      background: var(--app-surface);
      border: 1px solid var(--app-border);
      border-radius: 16px;
      padding: 2.25rem 2rem;
      box-shadow: 0 18px 45px -22px rgb(15 23 42 / 0.25);

      @media (max-width: 420px) {
        padding: 1.75rem 1.25rem;
      }
    }

    .auth-brand {
      text-align: center;
      margin-bottom: 1.75rem;

      h1 {
        margin: 0.9rem 0 0;
        font-size: 1.3rem;
        font-weight: 600;
        letter-spacing: -0.01em;
      }

      p {
        margin: 0.35rem 0 0;
        color: var(--app-text-muted);
        font-size: 0.9rem;
      }
    }

    .auth-brand-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 3rem;
      height: 3rem;
      border-radius: 12px;
      background: color-mix(in srgb, var(--p-primary-500, #10b981) 12%, transparent);
      color: var(--p-primary-600, #059669);
      font-size: 1.3rem;
    }
  `,
})
export class AuthCard {
  readonly title = input.required<string>();
  readonly subtitle = input<string>();

  protected readonly faShieldHalved = faShieldHalved;
}

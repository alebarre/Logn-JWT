import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { TooltipModule } from 'primeng/tooltip';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faBars,
  faBox,
  faIndustry,
  faRightFromBracket,
  faShieldHalved,
  faTags,
  faUsers,
  type IconDefinition,
} from '@fortawesome/free-solid-svg-icons';

import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

interface MenuItem {
  label: string;
  icon: IconDefinition;
  route: string;
}

/** Layout da área logada: topbar, navegação lateral (drawer no mobile) e conteúdo. */
@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    ButtonModule,
    DrawerModule,
    TooltipModule,
    FontAwesomeModule,
  ],
  templateUrl: './shell.html',
  styleUrl: './shell.scss',
})
export class Shell {
  private readonly auth = inject(AuthService);
  private readonly confirmation = inject(ConfirmationService);
  private readonly notification = inject(NotificationService);

  protected readonly drawerOpen = signal(false);
  protected readonly username = this.auth.username;

  protected readonly faBars = faBars;
  protected readonly faShieldHalved = faShieldHalved;
  protected readonly faRightFromBracket = faRightFromBracket;

  protected readonly menu: MenuItem[] = [
    { label: 'Clientes', icon: faUsers, route: '/clientes' },
    { label: 'Categorias', icon: faTags, route: '/categorias' },
    { label: 'Fabricantes', icon: faIndustry, route: '/fabricantes' },
    { label: 'Produtos', icon: faBox, route: '/produtos' },
  ];

  protected confirmLogout(): void {
    this.confirmation.confirm({
      header: 'Sair da aplicação',
      message: 'Deseja realmente encerrar a sessão?',
      acceptButtonProps: { label: 'Sair', severity: 'danger' },
      rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
      accept: () => {
        this.auth.logout();
        this.notification.info('Sessão encerrada. Até logo!');
      },
    });
  }
}

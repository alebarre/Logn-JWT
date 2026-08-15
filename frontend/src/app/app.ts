import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, ToastModule, ConfirmDialogModule],
  template: `
    <router-outlet />
    <p-toast position="top-right" [breakpoints]="{ '640px': { width: 'calc(100vw - 2rem)', right: '1rem' } }" />
    <p-confirmdialog [style]="{ width: 'min(26rem, 92vw)' }" />
  `,
})
export class App {}

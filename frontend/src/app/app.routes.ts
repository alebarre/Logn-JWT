import { Routes } from '@angular/router';

import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
    title: 'Entrar',
  },
  {
    path: 'registro',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
    title: 'Criar conta',
  },
  {
    path: 'recuperar-senha',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/forgot/forgot').then((m) => m.Forgot),
    title: 'Recuperar senha',
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./features/shell/shell').then((m) => m.Shell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'clientes' },
      {
        path: 'clientes',
        loadComponent: () => import('./features/clientes/clientes').then((m) => m.Clientes),
        title: 'Clientes',
      },
      {
        path: 'categorias',
        loadComponent: () => import('./features/categorias/categorias').then((m) => m.Categorias),
        title: 'Categorias',
      },
      {
        path: 'fabricantes',
        loadComponent: () => import('./features/fabricantes/fabricantes').then((m) => m.Fabricantes),
        title: 'Fabricantes',
      },
      {
        path: 'produtos',
        loadComponent: () => import('./features/produtos/produtos').then((m) => m.Produtos),
        title: 'Produtos',
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

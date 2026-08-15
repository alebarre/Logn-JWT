import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { CategoriasService } from '../../core/services/crud-api';
import { NomeDescricaoCrud } from '../shared/nome-descricao-crud';

@Component({
  selector: 'app-categorias',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NomeDescricaoCrud],
  template: `
    <app-nome-descricao-crud
      title="Categorias"
      entityLabel="Categoria"
      [feminine]="true"
      [service]="categoriasService"
    />
  `,
})
export class Categorias {
  protected readonly categoriasService = inject(CategoriasService);
}

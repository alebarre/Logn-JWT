import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { FabricantesService } from '../../core/services/crud-api';
import { NomeDescricaoCrud } from '../shared/nome-descricao-crud';

@Component({
  selector: 'app-fabricantes',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NomeDescricaoCrud],
  template: `
    <app-nome-descricao-crud
      title="Fabricantes"
      entityLabel="Fabricante"
      [service]="fabricantesService"
    />
  `,
})
export class Fabricantes {
  protected readonly fabricantesService = inject(FabricantesService);
}

import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import type { ViaCepResponse } from '../models/api.models';

/** Consulta de endereço por CEP na API pública ViaCEP. */
@Injectable({ providedIn: 'root' })
export class ViaCepService {
  private readonly http = inject(HttpClient);

  /** Busca o endereço do CEP (8 dígitos). Emite null quando o CEP não existe. */
  lookup(cep: string): Observable<ViaCepResponse | null> {
    const digits = cep.replace(/\D/g, '');
    return this.http
      .get<ViaCepResponse>(`https://viacep.com.br/ws/${digits}/json/`)
      .pipe(map((response) => (response.erro ? null : response)));
  }
}

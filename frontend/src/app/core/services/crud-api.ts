import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE } from '../config/app-config';
import type {
  Categoria,
  CategoriaRequest,
  Cliente,
  ClienteRequest,
  Fabricante,
  FabricanteRequest,
  Produto,
  ProdutoRequest,
} from '../models/api.models';

/** Base genérica para os CRUDs da API. */
abstract class CrudApi<T, TRequest> {
  protected readonly http = inject(HttpClient);
  protected abstract readonly baseUrl: string;

  list(): Observable<T[]> {
    return this.http.get<T[]>(this.baseUrl);
  }

  create(request: TRequest): Observable<T> {
    return this.http.post<T>(this.baseUrl, request);
  }

  update(id: number, request: TRequest): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

@Injectable({ providedIn: 'root' })
export class ClientesService extends CrudApi<Cliente, ClienteRequest> {
  protected readonly baseUrl = `${API_BASE}/clientes`;
}

@Injectable({ providedIn: 'root' })
export class CategoriasService extends CrudApi<Categoria, CategoriaRequest> {
  protected readonly baseUrl = `${API_BASE}/categorias`;
}

@Injectable({ providedIn: 'root' })
export class FabricantesService extends CrudApi<Fabricante, FabricanteRequest> {
  protected readonly baseUrl = `${API_BASE}/fabricantes`;
}

@Injectable({ providedIn: 'root' })
export class ProdutosService extends CrudApi<Produto, ProdutoRequest> {
  protected readonly baseUrl = `${API_BASE}/produtos`;
}

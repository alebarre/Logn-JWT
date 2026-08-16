import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEye, faPen, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';

import { AuthService } from '../../core/services/auth.service';
import { CategoriasService, FabricantesService, ProdutosService } from '../../core/services/crud-api';
import { NotificationService } from '../../core/services/notification.service';
import { applyServerValidationErrors, controlErrorMessage } from '../../core/utils/form-errors';
import type { Categoria, Fabricante, Produto } from '../../core/models/api.models';

@Component({
  selector: 'app-produtos',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    ButtonModule,
    DialogModule,
    InputNumberModule,
    InputTextModule,
    SelectModule,
    TableModule,
    FontAwesomeModule,
  ],
  templateUrl: './produtos.html',
})
export class Produtos implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly produtosService = inject(ProdutosService);
  private readonly categoriasService = inject(CategoriasService);
  private readonly fabricantesService = inject(FabricantesService);
  private readonly confirmation = inject(ConfirmationService);
  private readonly notification = inject(NotificationService);

  /** Apenas ADMIN vê as ações de criar, editar e excluir; visualizar é para todos. */
  protected readonly isAdmin = inject(AuthService).isAdmin;

  protected readonly faPlus = faPlus;
  protected readonly faPen = faPen;
  protected readonly faTrash = faTrash;
  protected readonly faEye = faEye;

  protected readonly produtos = signal<Produto[]>([]);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly fabricantes = signal<Fabricante[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly dialogVisible = signal(false);
  protected readonly editing = signal<Produto | null>(null);
  /** Produto exibido no dialog de detalhes; null = dialog fechado. */
  protected readonly viewing = signal<Produto | null>(null);

  protected readonly form = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    categoriaId: [null as number | null, Validators.required],
    fabricanteId: [null as number | null, Validators.required],
    numSerie: [''],
    preco: [null as number | null, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.load();
  }

  protected errorOf(name: string): string | null {
    return controlErrorMessage(this.form.get(name));
  }

  protected load(): void {
    this.loading.set(true);
    forkJoin({
      produtos: this.produtosService.list(),
      categorias: this.categoriasService.list(),
      fabricantes: this.fabricantesService.list(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ produtos, categorias, fabricantes }) => {
          this.produtos.set(produtos);
          this.categorias.set(categorias);
          this.fabricantes.set(fabricantes);
        },
        error: (error: unknown) => this.notification.apiError(error),
      });
  }

  protected openView(produto: Produto): void {
    this.viewing.set(produto);
  }

  protected openCreate(): void {
    this.editing.set(null);
    this.form.reset({ nome: '', categoriaId: null, fabricanteId: null, numSerie: '', preco: null });
    this.dialogVisible.set(true);
  }

  protected openEdit(produto: Produto): void {
    this.editing.set(produto);
    this.form.reset({
      nome: produto.nome,
      categoriaId: produto.categoria?.id ?? null,
      fabricanteId: produto.fabricantes?.id ?? null,
      numSerie: produto.numSerie ?? '',
      preco: produto.preco ?? null,
    });
    this.dialogVisible.set(true);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request = {
      nome: value.nome,
      categoriaId: value.categoriaId!,
      fabricanteId: value.fabricanteId!,
      numSerie: value.numSerie || null,
      preco: value.preco!,
    };
    const editing = this.editing();
    const operation = editing
      ? this.produtosService.update(editing.id, request)
      : this.produtosService.create(request);

    this.saving.set(true);
    operation.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => {
        this.notification.success(`Produto ${editing ? 'atualizado' : 'criado'} com sucesso.`);
        this.dialogVisible.set(false);
        this.load();
      },
      error: (error: unknown) => {
        if (!applyServerValidationErrors(this.form, error)) {
          this.notification.apiError(error);
        }
      },
    });
  }

  protected confirmDelete(produto: Produto): void {
    this.confirmation.confirm({
      header: 'Excluir produto',
      message: `Deseja realmente excluir "${produto.nome}"? Essa ação não pode ser desfeita.`,
      acceptButtonProps: { label: 'Excluir', severity: 'danger' },
      rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
      accept: () => this.delete(produto),
    });
  }

  private delete(produto: Produto): void {
    this.produtosService.delete(produto.id).subscribe({
      next: () => {
        this.notification.success('Produto excluído com sucesso.');
        this.load();
      },
      error: (error: unknown) => this.notification.apiError(error),
    });
  }
}

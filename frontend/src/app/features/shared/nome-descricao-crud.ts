import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, Observable } from 'rxjs';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TextareaModule } from 'primeng/textarea';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass, faPen, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';

import { NotificationService } from '../../core/services/notification.service';
import { applyServerValidationErrors, controlErrorMessage } from '../../core/utils/form-errors';

export interface NomeDescricaoItem {
  id: number;
  nome: string;
  descricao?: string;
}

export interface NomeDescricaoService {
  list(): Observable<NomeDescricaoItem[]>;
  create(request: { nome: string; descricao?: string | null }): Observable<NomeDescricaoItem>;
  update(id: number, request: { nome: string; descricao?: string | null }): Observable<NomeDescricaoItem>;
  delete(id: number): Observable<void>;
}

/**
 * CRUD genérico para entidades com nome + descrição (categorias e fabricantes):
 * tabela com busca, criação/edição em modal e exclusão com confirmação.
 */
@Component({
  selector: 'app-nome-descricao-crud',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    TableModule,
    TextareaModule,
    FontAwesomeModule,
  ],
  templateUrl: './nome-descricao-crud.html',
})
export class NomeDescricaoCrud implements OnInit {
  /** Título da página (plural), ex.: "Categorias". */
  readonly title = input.required<string>();
  /** Rótulo singular, ex.: "Categoria". */
  readonly entityLabel = input.required<string>();
  /** Gênero gramatical do rótulo, para as mensagens ("criada" vs "criado"). */
  readonly feminine = input(false);
  readonly service = input.required<NomeDescricaoService>();

  private readonly fb = inject(NonNullableFormBuilder);
  private readonly confirmation = inject(ConfirmationService);
  private readonly notification = inject(NotificationService);

  protected readonly faPlus = faPlus;
  protected readonly faPen = faPen;
  protected readonly faTrash = faTrash;
  protected readonly faMagnifyingGlass = faMagnifyingGlass;

  protected readonly items = signal<NomeDescricaoItem[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly dialogVisible = signal(false);
  protected readonly editing = signal<NomeDescricaoItem | null>(null);

  protected readonly dialogTitle = computed(() =>
    this.editing()
      ? `Editar ${this.entityLabel().toLowerCase()}`
      : `Nov${this.feminine() ? 'a' : 'o'} ${this.entityLabel().toLowerCase()}`,
  );

  protected readonly form = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    descricao: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  protected errorOf(name: string): string | null {
    return controlErrorMessage(this.form.get(name));
  }

  protected load(): void {
    this.loading.set(true);
    this.service()
      .list()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (items) => this.items.set(items),
        error: (error: unknown) => this.notification.apiError(error),
      });
  }

  protected openCreate(): void {
    this.editing.set(null);
    this.form.reset({ nome: '', descricao: '' });
    this.dialogVisible.set(true);
  }

  protected openEdit(item: NomeDescricaoItem): void {
    this.editing.set(item);
    this.form.reset({ nome: item.nome, descricao: item.descricao ?? '' });
    this.dialogVisible.set(true);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request = { nome: value.nome, descricao: value.descricao || null };
    const editing = this.editing();
    const operation = editing
      ? this.service().update(editing.id, request)
      : this.service().create(request);

    this.saving.set(true);
    operation.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => {
        const suffix = this.feminine() ? 'a' : 'o';
        this.notification.success(
          `${this.entityLabel()} ${editing ? `atualizad${suffix}` : `criad${suffix}`} com sucesso.`,
        );
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

  protected confirmDelete(item: NomeDescricaoItem): void {
    this.confirmation.confirm({
      header: `Excluir ${this.entityLabel().toLowerCase()}`,
      message: `Deseja realmente excluir "${item.nome}"? Essa ação não pode ser desfeita.`,
      acceptButtonProps: { label: 'Excluir', severity: 'danger' },
      rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
      accept: () => this.delete(item),
    });
  }

  private delete(item: NomeDescricaoItem): void {
    this.service()
      .delete(item.id)
      .subscribe({
        next: () => {
          this.notification.success(
            `${this.entityLabel()} excluíd${this.feminine() ? 'a' : 'o'} com sucesso.`,
          );
          this.load();
        },
        error: (error: unknown) => this.notification.apiError(error),
      });
  }
}

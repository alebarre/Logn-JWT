import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import {
  FormArray,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { finalize } from 'rxjs';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faLocationDot, faPen, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';

import { AuthService } from '../../core/services/auth.service';
import { ClientesService } from '../../core/services/crud-api';
import { ViaCepService } from '../../core/services/via-cep.service';
import { NotificationService } from '../../core/services/notification.service';
import { applyServerValidationErrors, controlErrorMessage } from '../../core/utils/form-errors';
import type { Cliente, Endereco } from '../../core/models/api.models';

@Component({
  selector: 'app-clientes',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    TableModule,
    TooltipModule,
    FontAwesomeModule,
  ],
  templateUrl: './clientes.html',
  styles: `
    .endereco-card {
      border: 1px solid var(--app-border);
      border-radius: 10px;
      padding: 1rem;
      margin-bottom: 1rem;
    }

    .endereco-card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 0.75rem;

      h3 {
        margin: 0;
        font-size: 0.95rem;
        font-weight: 600;
      }
    }
  `,
})
export class Clientes implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly clientesService = inject(ClientesService);
  private readonly viaCep = inject(ViaCepService);
  private readonly confirmation = inject(ConfirmationService);
  private readonly notification = inject(NotificationService);

  /** Apenas ADMIN vê as ações de criar, editar e excluir. */
  protected readonly isAdmin = inject(AuthService).isAdmin;

  protected readonly faPlus = faPlus;
  protected readonly faPen = faPen;
  protected readonly faTrash = faTrash;
  protected readonly faLocationDot = faLocationDot;

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly dialogVisible = signal(false);
  protected readonly editing = signal<Cliente | null>(null);
  protected readonly cepLoading = signal<number | null>(null);

  protected readonly form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    sobrenome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    telefone: [''],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    enderecos: this.fb.array<FormGroup>([]),
  });

  protected get enderecos(): FormArray<FormGroup> {
    return this.form.controls.enderecos;
  }

  ngOnInit(): void {
    this.load();
  }

  protected errorOf(name: string): string | null {
    return controlErrorMessage(this.form.get(name));
  }

  protected enderecoErrorOf(index: number, name: string): string | null {
    return controlErrorMessage(this.enderecos.at(index).get(name));
  }

  protected load(): void {
    this.loading.set(true);
    this.clientesService
      .list()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (clientes) => this.clientes.set(clientes),
        error: (error: unknown) => this.notification.apiError(error),
      });
  }

  protected cidadePrincipal(cliente: Cliente): string {
    const endereco = cliente.enderecos[0];
    return endereco ? `${endereco.localidade}/${endereco.uf}` : '—';
  }

  protected openCreate(): void {
    this.editing.set(null);
    this.resetForm();
    this.addEndereco();
    this.dialogVisible.set(true);
  }

  protected openEdit(cliente: Cliente): void {
    this.editing.set(cliente);
    this.resetForm();
    this.form.patchValue({
      nome: cliente.nome,
      sobrenome: cliente.sobrenome,
      telefone: cliente.telefone ?? '',
      email: cliente.email,
    });
    for (const endereco of cliente.enderecos) {
      this.addEndereco(endereco);
    }
    if (cliente.enderecos.length === 0) {
      this.addEndereco();
    }
    this.dialogVisible.set(true);
  }

  protected addEndereco(endereco?: Endereco): void {
    this.enderecos.push(
      this.fb.group({
        cep: [endereco?.cep ?? '', [Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],
        logradouro: [endereco?.logradouro ?? '', [Validators.required, Validators.maxLength(150)]],
        numero: [endereco?.numero ?? '', Validators.maxLength(20)],
        complemento: [endereco?.complemento ?? '', Validators.maxLength(100)],
        bairro: [endereco?.bairro ?? '', Validators.maxLength(100)],
        localidade: [endereco?.localidade ?? '', [Validators.required, Validators.maxLength(100)]],
        uf: [endereco?.uf ?? '', [Validators.required, Validators.pattern(/^[A-Za-z]{2}$/)]],
      }),
    );
  }

  protected removeEndereco(index: number): void {
    if (this.enderecos.length <= 1) {
      this.notification.warn('O cliente precisa de ao menos um endereço.');
      return;
    }
    this.enderecos.removeAt(index);
  }

  /** Busca o endereço no ViaCEP quando o campo CEP é preenchido. */
  protected lookupCep(index: number): void {
    const group = this.enderecos.at(index);
    const cep = String(group.get('cep')?.value ?? '');
    if (cep.replace(/\D/g, '').length !== 8) {
      return;
    }
    this.cepLoading.set(index);
    this.viaCep
      .lookup(cep)
      .pipe(finalize(() => this.cepLoading.set(null)))
      .subscribe({
        next: (endereco) => {
          if (!endereco) {
            this.notification.warn('CEP não encontrado. Preencha o endereço manualmente.');
            return;
          }
          group.patchValue({
            logradouro: endereco.logradouro,
            bairro: endereco.bairro,
            localidade: endereco.localidade,
            uf: endereco.uf,
          });
        },
        error: () => this.notification.warn('Não foi possível consultar o CEP. Preencha manualmente.'),
      });
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request = {
      nome: value.nome,
      sobrenome: value.sobrenome,
      telefone: value.telefone || null,
      email: value.email,
      enderecos: value.enderecos as Endereco[],
    };
    const editing = this.editing();
    const operation = editing
      ? this.clientesService.update(editing.id, request)
      : this.clientesService.create(request);

    this.saving.set(true);
    operation.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => {
        this.notification.success(`Cliente ${editing ? 'atualizado' : 'criado'} com sucesso.`);
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

  protected confirmDelete(cliente: Cliente): void {
    this.confirmation.confirm({
      header: 'Excluir cliente',
      message: `Deseja realmente excluir "${cliente.nome} ${cliente.sobrenome}"? Essa ação não pode ser desfeita.`,
      acceptButtonProps: { label: 'Excluir', severity: 'danger' },
      rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
      accept: () => this.delete(cliente),
    });
  }

  private delete(cliente: Cliente): void {
    this.clientesService.delete(cliente.id).subscribe({
      next: () => {
        this.notification.success('Cliente excluído com sucesso.');
        this.load();
      },
      error: (error: unknown) => this.notification.apiError(error),
    });
  }

  private resetForm(): void {
    this.form.reset({ nome: '', sobrenome: '', telefone: '', email: '' });
    this.enderecos.clear();
  }
}

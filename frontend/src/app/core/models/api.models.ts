/** Modelos espelhando os DTOs do backend (mapeados na borda da API). */

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface MessageResponse {
  message: string;
}

/** Estrutura padrão de erro da API (ApiErrorDTO). */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  /** Presente apenas em erros de validação: campo -> mensagem. */
  errors?: Record<string, string>;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

export interface ConfirmRegisterRequest {
  email: string;
  code: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  code: string;
  newPassword: string;
}

export interface Endereco {
  id?: number;
  cep: string;
  logradouro: string;
  numero?: string;
  complemento?: string;
  bairro?: string;
  localidade: string;
  uf: string;
}

export interface Cliente {
  id: number;
  nome: string;
  sobrenome: string;
  telefone?: string;
  email: string;
  enderecos: Endereco[];
  dataCadastro?: string;
}

export interface ClienteRequest {
  nome: string;
  sobrenome: string;
  telefone?: string | null;
  email: string;
  enderecos: Endereco[];
}

export interface Categoria {
  id: number;
  nome: string;
  descricao?: string;
}

export interface CategoriaRequest {
  nome: string;
  descricao?: string | null;
}

export interface Fabricante {
  id: number;
  nome: string;
  descricao?: string;
}

export interface FabricanteRequest {
  nome: string;
  descricao?: string | null;
}

export interface Produto {
  id: number;
  nome: string;
  categoria?: Categoria;
  numSerie?: string;
  preco?: number;
  fabricantes?: Fabricante;
  dataCadastro?: string;
}

export interface ProdutoRequest {
  nome: string;
  categoriaId: number;
  numSerie?: string | null;
  preco: number;
  fabricanteId: number;
}

export interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

/**
 * Configurações da aplicação.
 * As URLs de API são relativas: em desenvolvimento o proxy (proxy.conf.json)
 * encaminha para o backend em localhost:8080; em produção espera-se o app
 * servido sob o mesmo domínio do backend (ou atrás de um reverse proxy).
 */
export const APP_CONFIG = {
  /** Validade (em segundos) dos códigos de verificação enviados por e-mail. */
  verificationCodeTtlSeconds: 60,
  /** Tamanho do código de verificação. */
  verificationCodeLength: 5,
} as const;

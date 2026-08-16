import { ENV } from './env.generated';

/**
 * Prefixo de todas as chamadas à API. Evita colisão entre as rotas do SPA
 * (ex: /clientes aberto via F5) e os endpoints do backend: em dev o proxy
 * (proxy.conf.js) remove o prefixo e encaminha para o BACKEND_URL do .env; em
 * produção um reverse proxy deve rotear /api/* para o backend da mesma forma.
 *
 * Os valores vêm do .env único na raiz do projeto, via env.generated.ts
 * (gerado por scripts/generate-env.js antes de start/build/test).
 */
export const API_BASE = ENV.apiBase;

/** Configurações da aplicação. */
export const APP_CONFIG = {
  /** Validade (em segundos) dos códigos de verificação enviados por e-mail. */
  verificationCodeTtlSeconds: ENV.verificationCodeTtlSeconds,
  /** Tamanho do código de verificação. */
  verificationCodeLength: ENV.verificationCodeLength,
} as const;

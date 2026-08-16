/**
 * Proxy de desenvolvimento (ng serve): encaminha as chamadas ao API_BASE para
 * o backend, removendo o prefixo. Alvo e prefixo vêm do .env único na raiz.
 */
const { loadEnv } = require('./scripts/env');

const env = loadEnv();
const apiBase = env.API_BASE || '/api';
const target = env.BACKEND_URL || 'http://localhost:8080';

module.exports = {
  [apiBase]: {
    target,
    secure: false,
    pathRewrite: { [`^${apiBase}`]: '' },
  },
};

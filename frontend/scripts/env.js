/**
 * Leitura do .env único do projeto (na raiz do repositório).
 * Usado pelo proxy de dev (proxy.conf.js) e pelo gerador de config
 * (scripts/generate-env.js) — sem dependência externa de dotenv.
 */
const fs = require('fs');
const path = require('path');

const ENV_PATH = path.resolve(__dirname, '..', '..', '.env');

function loadEnv() {
  const vars = {};
  if (!fs.existsSync(ENV_PATH)) {
    return vars;
  }
  for (const line of fs.readFileSync(ENV_PATH, 'utf8').split(/\r?\n/)) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) continue;
    const match = trimmed.match(/^([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$/);
    if (match) {
      vars[match[1]] = match[2].replace(/^(['"])(.*)\1$/, '$2');
    }
  }
  return vars;
}

module.exports = { loadEnv, ENV_PATH };

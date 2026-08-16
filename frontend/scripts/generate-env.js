/**
 * Gera src/app/core/config/env.generated.ts a partir do .env único na raiz
 * do projeto. Executado automaticamente antes de start/build/test (hooks
 * "pre*" do npm) e após npm install.
 */
const fs = require('fs');
const path = require('path');
const { loadEnv, ENV_PATH } = require('./env');

const env = loadEnv();

const config = {
  apiBase: env.API_BASE || '/api',
  verificationCodeTtlSeconds: Number(env.VERIFICATION_CODE_EXPIRATION_SECONDS) || 60,
  verificationCodeLength: Number(env.VERIFICATION_CODE_LENGTH) || 5,
};

const outFile = path.resolve(__dirname, '..', 'src', 'app', 'core', 'config', 'env.generated.ts');

const content = `// ARQUIVO GERADO AUTOMATICAMENTE por scripts/generate-env.js a partir do .env
// na raiz do projeto. Não edite manualmente — altere o .env e rode
// "node scripts/generate-env.js" (ou npm start/build, que já executam o script).
export const ENV = ${JSON.stringify(config, null, 2)} as const;
`;

fs.writeFileSync(outFile, content);
console.log(
  `[generate-env] ${path.relative(process.cwd(), outFile)} gerado a partir de ${
    fs.existsSync(ENV_PATH) ? ENV_PATH : 'valores padrão (.env não encontrado)'
  }`
);

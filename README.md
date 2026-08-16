# Login JWT

Aplicação full stack organizada em monorepo: API REST em **Spring Boot 4.1.0 + Java 25** (`backend/`) e interface web em **Angular 20** (`frontend/`), utilizando **JWT**, **Spring Security**, **JPA**, **PostgreSQL**, **Swagger/OpenAPI** e **Docker**.

A aplicação implementa:
- Autenticação com JWT (access + refresh token)
- Registro em duas etapas com código de verificação de 5 dígitos enviado por e-mail
- Recuperação de senha ("esqueci minha senha") também com código por e-mail
- Controle de acesso baseado em roles (`ADMIN` / `USER`)
- CRUD completo de Produtos, Categorias e Fabricantes
- Documentação automática via Swagger

---

## Tecnologias

**Backend (`backend/`):**

- Java 25
- Spring Boot 4.1.0
- Spring Security
- JWT (jjwt 0.12.6)
- Spring Data JPA
- Spring Mail (`spring-boot-starter-mail`) — envio dos códigos de verificação
- Bean Validation (Jakarta Validation / `spring-boot-starter-validation`)
- PostgreSQL
- Docker & Docker Compose
- Swagger / OpenAPI
- Lombok

**Frontend (`frontend/`):**

- Angular 20 (standalone components)
- PrimeNG 20 (componentes de UI, tema Aura)
- Font Awesome (ícones)
- RxJS

---

## Pré-requisitos

Antes de começar, instale na sua máquina:

1. **[Git](https://git-scm.com/downloads)** — para clonar o repositório.
2. **[JDK 25](https://adoptium.net/temurin/releases/?version=25)** (Java Development Kit) — instale e confira com:
   ```bash
   java -version
   ```
   O comando deve mostrar algo como `openjdk version "25..."`.
3. **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (inclui o Docker Compose) — usado para subir o banco de dados PostgreSQL sem precisar instalá-lo manualmente. Confira com:
   ```bash
   docker --version
   docker compose version
   ```
4. **[Node.js](https://nodejs.org/) 24 ou superior** (inclui o npm) — necessário apenas para rodar o frontend Angular. Confira com:
   ```bash
   node --version
   npm --version
   ```

Não é necessário instalar o Maven: o projeto já traz o Maven Wrapper (`mvnw`), que baixa a versão correta automaticamente. O Angular CLI também não precisa ser instalado globalmente — os comandos do frontend usam a versão local via `npm`.

---

## 1. Clonar o repositório

```bash
git clone <URL-do-repositorio>
cd Logn-JWT
```

O repositório é organizado como um monorepo:

```
Logn-JWT/
├── backend/    # API Spring Boot (Java 25)
└── frontend/   # Aplicação Angular
```

Os comandos das próximas seções referentes ao back-end devem ser executados dentro da pasta `backend/`:

```bash
cd backend
```

---

## 2. Configurar as variáveis de ambiente

O projeto usa um **único arquivo `.env` na raiz do repositório** (não versionado) para guardar todas as variáveis de ambiente do back-end **e** do front-end. Um modelo já está pronto em `.env.example`, na raiz. Basta copiá-lo (a partir da raiz do projeto):

```bash
cp .env.example .env
```

Os valores padrão do banco e do JWT já funcionam para rodar localmente. As variáveis de e-mail (`MAIL_USERNAME` e `MAIL_PASSWORD`) **precisam ser preenchidas** para que o registro e a recuperação de senha funcionem, pois ambos enviam um código de verificação por e-mail:

| Variável | Para que serve | Valor padrão |
|---|---|---|
| `POSTGRES_DB` | Nome do banco de dados | `login_jwt_db` |
| `POSTGRES_USER` | Usuário do banco | `login_jwt_user` |
| `POSTGRES_PASSWORD` | Senha do banco | `login_jwt_pass` |
| `POSTGRES_PORT` | Porta exposta pelo container do Postgres | `5433` |
| `JWT_SECRET` | Chave usada para assinar os tokens JWT | valor de exemplo — troque em produção |
| `JWT_ACCESS_EXPIRATION` | Validade do access token (ms) | `900000` (15 min) |
| `JWT_REFRESH_EXPIRATION` | Validade do refresh token (ms) | `604800000` (7 dias) |
| `MAIL_HOST` | Servidor SMTP para envio dos e-mails | `smtp.gmail.com` |
| `MAIL_PORT` | Porta do servidor SMTP | `587` |
| `MAIL_USERNAME` | E-mail remetente dos códigos | *(vazio — preencha)* |
| `MAIL_PASSWORD` | Senha do e-mail remetente | *(vazio — preencha)* |
| `VERIFICATION_CODE_EXPIRATION_SECONDS` | Validade dos códigos de verificação (segundos) — usado pelo back e pelo front | `60` (1 min) |
| `VERIFICATION_CODE_LENGTH` | Tamanho do código de verificação — usado pelo front | `5` |
| `API_BASE` | Prefixo das chamadas à API no front-end | `/api` |
| `BACKEND_URL` | URL do back-end usada pelo proxy de dev do front (`ng serve`) | `http://localhost:8080` |

> **Front-end:** os valores do `.env` chegam ao Angular pelo script `frontend/scripts/generate-env.js`, que gera `src/app/core/config/env.generated.ts` automaticamente antes de `npm start`/`npm run build`/`npm test` (e após `npm install`). O proxy de dev (`frontend/proxy.conf.js`) também lê o `.env` da raiz.

> **Gmail:** não use a senha normal da conta em `MAIL_PASSWORD` — gere uma [senha de app](https://myaccount.google.com/apppasswords) (requer verificação em duas etapas ativada).

---

## 3. Subir o banco de dados (PostgreSQL via Docker)

Com o Docker Desktop aberto, na **raiz do projeto** (onde ficam o `docker-compose.yml` e o `.env`) rode:

```bash
docker compose up -d
```

Isso cria um container PostgreSQL disponível em `localhost:5433`. Para conferir se subiu corretamente:

```bash
docker ps
```

Você deve ver o container `login_jwt_postgres` com status `Up`.

---

## 4. Buildar a aplicação

No Linux/macOS:
```bash
./mvnw clean package
```

No Windows:
```bash
mvnw.cmd clean package
```

Se aparecer erro de permissão (`Permission denied`) no Linux/macOS, rode `chmod +x mvnw` e tente de novo.

Isso baixa as dependências, compila o código e gera o arquivo `target/login-jwt-0.0.1-SNAPSHOT.jar`.

---

## 5. Rodar a aplicação

Você pode rodar de duas formas:

**Opção A — usando o Maven Wrapper (recomendado em desenvolvimento):**
```bash
JAVA_HOME=/caminho/para/jdk-25 ./mvnw spring-boot:run
```

**Opção B — executando o jar gerado no build:**
```bash
/caminho/para/jdk-25/bin/java -jar target/login-jwt-0.0.1-SNAPSHOT.jar
```

> Substitua `/caminho/para/jdk-25` pelo caminho do JDK 25 instalado (ex.: `/home/$USER/.jdks/openjdk-25.0.2`). O projeto foi compilado para Java 25 e **não roda em versões anteriores do JRE**.

Quando aparecer a mensagem `Started LoginJwtApplication` no terminal, a API está no ar em:

```
http://localhost:8080
```

---

## 6. Rodar o frontend (Angular)

Com a API no ar (passo 5), abra outro terminal e rode:

```bash
cd frontend
npm install   # apenas na primeira vez
npm start
```

O `npm start` sobe o servidor de desenvolvimento em:

```
http://localhost:4200
```

As chamadas à API (`/auth`, `/clientes`, `/categorias`, `/fabricantes`, `/produtos`) são redirecionadas automaticamente para `http://localhost:8080` pelo proxy de desenvolvimento ([frontend/proxy.conf.json](frontend/proxy.conf.json)) — não é preciso configurar CORS nem URLs.

Outros comandos úteis (sempre dentro de `frontend/`):

```bash
npm run build   # build de produção (saída em dist/frontend)
npm test        # testes unitários (Karma/Jasmine)
```

> **Produção:** as URLs de API do frontend são relativas — sirva o app no mesmo domínio do backend ou atrás de um reverse proxy que roteie os caminhos de API listados acima.

### Funcionalidades do frontend

- **Login** com redirecionamento para a rota original (`returnUrl`)
- **Registro em 2 etapas**: dados → código de 5 dígitos por e-mail (countdown de 60s + reenvio) → tokens e entrada direta na aplicação
- **Recuperação de senha em 2 etapas**: e-mail → código + nova senha
- **Sessão JWT**: access token no header via interceptor; em 401 o refresh token renova a sessão automaticamente (uma única vez, compartilhado entre chamadas concorrentes) antes de deslogar
- **CRUDs**: clientes (endereços com preenchimento automático via ViaCEP), categorias, fabricantes e produtos
- **UX**: toasts para avisos/respostas (PrimeNG Toast) e modais de confirmação para ações destrutivas (excluir, sair); formulários exibem os erros de validação do backend campo a campo (`ApiErrorDTO.errors`)
- **Responsivo**: sidebar fixa em desktop, drawer no mobile/tablet; tabelas ocultam colunas secundárias em telas estreitas (`hide-sm`/`hide-md`)

> **Observação sobre roles:** o access token do backend não carrega claim de roles (apenas `sub`/`exp`), então a UI não esconde ações restritas a ADMIN — um usuário USER que tentar criar, editar ou excluir recebe o 403 do backend com a mensagem exibida em toast. Se o backend passar a expor as roles (claim no JWT ou endpoint `/auth/me`), dá para ocultar os botões de escrita para quem é apenas USER.

---

## 7. Usuários para login

A aplicação **não vem com usuários pré-cadastrados** — o banco começa vazio e você precisa criar seus próprios usuários. Existem duas formas:

### Opção A — criar via API (registro em duas etapas)

O registro é feito em **duas etapas**, com confirmação por código enviado ao e-mail. O `username` **é o e-mail do usuário** (é para ele que os códigos são enviados), e todo usuário registrado recebe automaticamente a role `USER`.

**Etapa 1 — solicitar o registro.** O usuário ainda não é criado: os dados ficam pendentes e um código de 5 dígitos é enviado para o e-mail informado:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario@exemplo.com", "password": "user123"}'
```

Resposta:
```json
{ "message": "Código de confirmação enviado para o e-mail informado." }
```

**Etapa 2 — confirmar o código.** O código expira em 1 minuto (configurável via `VERIFICATION_CODE_EXPIRATION_SECONDS`). Ao confirmar, o usuário é criado e a resposta já traz os tokens:

```bash
curl -X POST http://localhost:8080/auth/register/confirm \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@exemplo.com", "code": "04371"}'
```

Se o código expirar, basta repetir a etapa 1 — um novo pedido substitui o código anterior.

### Opção B — criar um usuário ADMIN diretamente no banco

Como o registro público só cria usuários `USER`, para ter um usuário `ADMIN` de teste é preciso inserir direto no banco (ou registrar um usuário e depois promovê-lo). Rode a aplicação pelo menos uma vez antes (passo 5), para que as tabelas `users` e `user_roles` sejam criadas automaticamente.

Abra um terminal dentro do container do Postgres:

```bash
docker exec -it login_jwt_postgres psql -U login_jwt_user -d login_jwt_db
```

E cole o script abaixo, que cria um usuário `admin` (senha `admin123`) e um usuário `user` (senha `user123`), já com as senhas criptografadas em BCrypt:

```sql
INSERT INTO users (username, password) VALUES
  ('admin', '$2a$10$J6W7ZmZ7H9bV36NXYGFGXuAM672e0Cn3Jte26.QWqpocpwsc1MqUS'),
  ('user',  '$2a$10$Q35qcYhG7OZMGilOfwB6OecE0cYmQ/AhRseIfTsdruVdoKN.BjF1i');

INSERT INTO user_roles (user_id, role) VALUES
  ((SELECT id FROM users WHERE username = 'admin'), 'ROLE_ADMIN'),
  ((SELECT id FROM users WHERE username = 'user'),  'ROLE_USER');
```

Saia do psql com `\q`. Agora você pode logar com:

| Usuário | Senha | Role |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `user` | `user123` | `USER` |

Essas senhas são apenas para testes locais. Nunca use credenciais assim em produção.

> Usuários inseridos direto no banco com username que não seja um e-mail (como `admin` e `user` acima) fazem login normalmente, mas **não conseguem usar a recuperação de senha**, que envia o código para o username.

---

## 8. Fazendo login e usando o token

Login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

A resposta traz `accessToken` e `refreshToken`. Use o `accessToken` no cabeçalho `Authorization` das próximas requisições:

```bash
curl http://localhost:8080/produtos \
  -H "Authorization: Bearer <accessToken>"
```

Quando o access token expirar, gere um novo par de tokens com:
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refreshToken>"}'
```

---

## 9. Esqueci minha senha (recuperação por código)

A recuperação de senha segue o mesmo padrão do registro: um código de 5 dígitos é enviado ao e-mail (o username) e expira em 1 minuto.

**Etapa 1 — solicitar o código:**
```bash
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@exemplo.com"}'
```

Resposta:
```json
{ "message": "Código de recuperação enviado para o e-mail informado." }
```

**Etapa 2 — confirmar o código e definir a nova senha:**
```bash
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@exemplo.com", "code": "04371", "newPassword": "novaSenha123"}'
```

Resposta:
```json
{ "message": "Senha redefinida com sucesso." }
```

O código é de uso único: após a troca de senha ele é descartado, e um novo pedido de recuperação invalida o código anterior.

**Erros possíveis na confirmação (registro e recuperação):**

| Situação | Status | Mensagem |
|---|---|---|
| Código vazio | `400` | "Código é obrigatório" |
| Código com formato errado (não são 5 dígitos) | `400` | "Código deve conter exatamente 5 dígitos" |
| Código não confere | `400` | "Código inválido. Verifique o código enviado para o seu e-mail." |
| Código expirado (após 1 min) | `410` | "Código expirado. Solicite um novo código..." |
| E-mail não cadastrado | `404` | "E-mail não cadastrado: ..." |
| Falha no envio do e-mail (SMTP) | `503` | "Não foi possível enviar o e-mail com o código..." |

---

## 10. Exemplo: cadastrando categoria, fabricante e produto

Categoria e fabricante são entidades próprias (tabelas `categorias` e `fabricantes`), e o produto se relaciona com ambas por chave estrangeira. Por isso, ao criar um produto, a categoria e o fabricante referenciados **precisam já existir** — a API responde `404` se o `id` informado não for encontrado.

```bash
# 1. Criar uma categoria
curl -X POST http://localhost:8080/categorias \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"nome": "Notebooks", "descricao": "Computadores portáteis"}'

# 2. Criar um fabricante
curl -X POST http://localhost:8080/fabricantes \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"nome": "Dell", "descricao": "Fabricante de notebooks e desktops"}'

# 3. Criar o produto, referenciando os ids retornados acima
curl -X POST http://localhost:8080/produtos \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Notebook Dell Inspiron 15",
        "categoriaId": 1,
        "numSerie": "DL-INS15-0001",
        "preco": 4299.90,
        "fabricanteId": 1
      }'
```

A resposta traz a categoria e o fabricante já resolvidos (objeto completo, não só o id):

```json
{
  "id": 1,
  "nome": "Notebook Dell Inspiron 15",
  "categoria": { "id": 1, "nome": "Notebooks", "descricao": "Computadores portáteis" },
  "numSerie": "DL-INS15-0001",
  "preco": 4299.9,
  "fabricantes": { "id": 1, "nome": "Dell", "descricao": "Fabricante de notebooks e desktops" },
  "dataCadastro": "2026-08-12T22:10:00"
}
```

---

## 11. Documentação interativa (Swagger)

Com a aplicação rodando, acesse no navegador:

```
http://localhost:8080/swagger-ui/index.html
```

Lá é possível ver e testar todos os endpoints (autenticação, produtos, categorias e fabricantes).

---

## Endpoints principais

| Método | Rota | Acesso |
|---|---|---|
| POST | `/auth/register` | Público — etapa 1: envia o código de confirmação por e-mail |
| POST | `/auth/register/confirm` | Público — etapa 2: confirma o código, cria o usuário e retorna os tokens |
| POST | `/auth/login` | Público |
| POST | `/auth/refresh` | Público |
| POST | `/auth/forgot-password` | Público — envia o código de recuperação por e-mail |
| POST | `/auth/reset-password` | Público — confirma o código e redefine a senha |
| GET | `/produtos`, `/categorias`, `/fabricantes` | `USER` ou `ADMIN` |
| POST/PUT/DELETE | `/produtos`, `/categorias`, `/fabricantes` | Apenas `ADMIN` |

---

## Validação e tratamento de erros

Todas as respostas de erro seguem o mesmo formato JSON, pensado para ser consumido diretamente por um frontend:

```json
{
  "timestamp": "2026-08-12T21:54:46Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos campos enviados.",
  "path": "/produtos",
  "errors": {
    "nome": "Nome é obrigatório",
    "preco": "Preço deve ser um valor numérico maior que zero"
  }
}
```

O campo `errors` (mapa campo → mensagem) só aparece em erros de validação de formulário, permitindo destacar cada campo individualmente no frontend. Os demais erros trazem apenas `message`.

| Situação | Status |
|---|---|
| Campo obrigatório vazio, fora do tamanho permitido ou em formato inválido (ex.: nome só com números, preço negativo) | `400` |
| Corpo da requisição mal formado (JSON inválido) | `400` |
| Código de verificação vazio, com formato errado ou inválido | `400` |
| `categoriaId` ou `fabricanteId` não encontrado / e-mail não cadastrado | `404` |
| Credenciais inválidas / token inválido ou expirado | `401` |
| Usuário sem permissão para a ação | `403` |
| Username (e-mail) já cadastrado no registro | `409` |
| Código de verificação expirado | `410` |
| Falha no envio do e-mail com o código (SMTP) | `503` |

---

## Solução de problemas comuns

- **`docker compose up` falha ou porta em uso**: verifique se já existe algo rodando na porta `5433` (`sudo lsof -i :5433` no Linux/macOS) ou altere `POSTGRES_PORT` no `.env`.
- **Aplicação não conecta ao banco**: confira se o container está de pé (`docker ps`) e se `SPRING_DATASOURCE_URL` no `.env` aponta para a mesma porta configurada em `POSTGRES_PORT`.
- **`UnsupportedClassVersionError` / "class file version 69.0"**: o jar foi compilado com Java 25 mas está sendo executado com uma JVM mais antiga. Certifique-se de usar JDK 25 (veja passo 5).
- **`release version 25 not supported`**: o Maven está rodando com JDK 21 ou inferior. Defina `JAVA_HOME` para o JDK 25 antes de chamar `./mvnw`.
- **`./mvnw: Permission denied`**: rode `chmod +x mvnw`.
- **Porta 8080 já em uso**: pare o processo que está usando a porta ou rode com `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.
- **`503` ao registrar ou recuperar senha ("Não foi possível enviar o e-mail...")**: `MAIL_USERNAME`/`MAIL_PASSWORD` não foram preenchidos no `.env`, ou a senha está incorreta. Para Gmail, use uma senha de app (veja o passo 2).

---

## Estrutura do Projeto

```
backend/src/main/java/com/br/login_jwt/
├── config/       # Configuração de segurança e Swagger
├── controller/   # Endpoints REST (Auth, Produto, Categoria, Fabricantes)
├── DTO/          # Objetos de transferência de dados (Request/Response separados dos models JPA)
├── exception/    # Tratamento global de erros
├── model/        # Entidades JPA (User, PendingRegistration, PasswordResetCode, Produto, ...)
├── repository/   # Repositórios Spring Data JPA
├── security/     # Filtro JWT, JwtService, UserDetailsService
├── service/      # Regras de negócio (Auth, PasswordReset, Email, Produto, ...)
└── util/         # Utilitários (gerador de código de verificação, hash)

backend/src/main/resources/templates/email/   # Templates HTML dos e-mails de código de verificação

frontend/src/app/
├── core/         # Infraestrutura: config, guards de rota, interceptors HTTP (JWT), models, services, utils
└── features/     # Telas por domínio: auth, clientes, categorias, fabricantes, produtos, shell, shared

frontend/proxy.conf.json              # Proxy do dev server: redireciona as chamadas de API para localhost:8080
```

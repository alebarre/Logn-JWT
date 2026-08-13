# Login JWT API

API REST desenvolvida em **Spring Boot 4.1.0 + Java 25**, utilizando **JWT**, **Spring Security**, **JPA**, **PostgreSQL**, **Swagger/OpenAPI** e **Docker**.

A aplicação implementa:
- Autenticação com JWT (access + refresh token)
- Controle de acesso baseado em roles (`ADMIN` / `USER`)
- CRUD completo de Produtos, Categorias e Fabricantes
- Documentação automática via Swagger

---

## Tecnologias

- Java 25
- Spring Boot 4.1.0
- Spring Security
- JWT (jjwt 0.12.6)
- Spring Data JPA
- Bean Validation (Jakarta Validation / `spring-boot-starter-validation`)
- PostgreSQL
- Docker & Docker Compose
- Swagger / OpenAPI
- Lombok

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

Não é necessário instalar o Maven: o projeto já traz o Maven Wrapper (`mvnw`), que baixa a versão correta automaticamente.

---

## 1. Clonar o repositório

```bash
git clone <URL-do-repositorio>
cd Logn-JWT
```

---

## 2. Configurar as variáveis de ambiente

O projeto usa um arquivo `.env` (não versionado) para guardar as credenciais do banco e a chave do JWT. Um modelo já está pronto em `.env.example`. Basta copiá-lo:

```bash
cp .env.example .env
```

Os valores padrão já funcionam para rodar localmente — não é necessário alterar nada para o primeiro teste. Se quiser, abra o `.env` e ajuste:

| Variável | Para que serve | Valor padrão |
|---|---|---|
| `POSTGRES_DB` | Nome do banco de dados | `login_jwt_db` |
| `POSTGRES_USER` | Usuário do banco | `login_jwt_user` |
| `POSTGRES_PASSWORD` | Senha do banco | `login_jwt_pass` |
| `POSTGRES_PORT` | Porta exposta pelo container do Postgres | `5433` |
| `JWT_SECRET` | Chave usada para assinar os tokens JWT | valor de exemplo — troque em produção |
| `JWT_ACCESS_EXPIRATION` | Validade do access token (ms) | `900000` (15 min) |
| `JWT_REFRESH_EXPIRATION` | Validade do refresh token (ms) | `604800000` (7 dias) |

---

## 3. Subir o banco de dados (PostgreSQL via Docker)

Com o Docker Desktop aberto, na raiz do projeto rode:

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

## 6. Usuários para login

A aplicação **não vem com usuários pré-cadastrados** — o banco começa vazio e você precisa criar seus próprios usuários. Existem duas formas:

### Opção A — criar via API (`/auth/register`)

Todo usuário registrado por esse endpoint recebe automaticamente a role `USER`. Com a aplicação rodando, execute:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario", "password": "user123"}'
```

A resposta traz o `accessToken` e o `refreshToken` já prontos para uso — ou você pode logar depois em `/auth/login` com o mesmo usuário/senha.

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

---

## 7. Fazendo login e usando o token

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

## 8. Exemplo: cadastrando categoria, fabricante e produto

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

## 9. Documentação interativa (Swagger)

Com a aplicação rodando, acesse no navegador:

```
http://localhost:8080/swagger-ui/index.html
```

Lá é possível ver e testar todos os endpoints (autenticação, produtos, categorias e fabricantes).

---

## Endpoints principais

| Método | Rota | Acesso |
|---|---|---|
| POST | `/auth/register` | Público |
| POST | `/auth/login` | Público |
| POST | `/auth/refresh` | Público |
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
| `categoriaId` ou `fabricanteId` não encontrado | `404` |
| Credenciais inválidas / token inválido ou expirado | `401` |
| Usuário sem permissão para a ação | `403` |

---

## Solução de problemas comuns

- **`docker compose up` falha ou porta em uso**: verifique se já existe algo rodando na porta `5433` (`sudo lsof -i :5433` no Linux/macOS) ou altere `POSTGRES_PORT` no `.env`.
- **Aplicação não conecta ao banco**: confira se o container está de pé (`docker ps`) e se `SPRING_DATASOURCE_URL` no `.env` aponta para a mesma porta configurada em `POSTGRES_PORT`.
- **`UnsupportedClassVersionError` / "class file version 69.0"**: o jar foi compilado com Java 25 mas está sendo executado com uma JVM mais antiga. Certifique-se de usar JDK 25 (veja passo 5).
- **`release version 25 not supported`**: o Maven está rodando com JDK 21 ou inferior. Defina `JAVA_HOME` para o JDK 25 antes de chamar `./mvnw`.
- **`./mvnw: Permission denied`**: rode `chmod +x mvnw`.
- **Porta 8080 já em uso**: pare o processo que está usando a porta ou rode com `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.

---

## Estrutura do Projeto

```
src/main/java/com/br/login_jwt/
├── config/       # Configuração de segurança e Swagger
├── controller/   # Endpoints REST (Auth, Produto, Categoria, Fabricantes)
├── DTO/          # Objetos de transferência de dados (Request/Response separados dos models JPA)
├── exception/    # Tratamento global de erros
├── model/        # Entidades JPA (User, Produto, Categoria, Fabricantes)
├── repository/   # Repositórios Spring Data JPA
├── security/     # Filtro JWT, JwtService, UserDetailsService
└── service/      # Regras de negócio
```

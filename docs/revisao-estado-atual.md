# Revisão de estado atual — Rest Assured (aprendizado)

Data: 2026-10-09 (atualizado — refatoração de estrutura + clients/fixtures/schemas)

## Arquivos revisados

- `src/test/java/com/financecontrol/config/RestAssuredConfig.java` (15 linhas)
- `src/test/java/com/financecontrol/clients/authentication/AuthClient.java` (29 linhas)
- `src/test/java/com/financecontrol/fixtures/authentication/LoginFixture.java` (14 linhas)
- `src/test/java/com/financecontrol/fixtures/authentication/UserRegistrationFixture.java` (174 linhas)
- `src/test/java/com/financecontrol/models/authentication/LoginRequest.java`
- `src/test/java/com/financecontrol/models/authentication/UserRegistrationRequest.java`
- `src/test/java/com/financecontrol/tests/authentication/AuthenticationTest.java` (180 linhas, 15 testes)
- `src/test/java/com/financecontrol/tests/authentication/UserRegistrationTest.java` (492 linhas, 27 testes)
- `src/test/resources/schemas/authentication/*.json` (2 schemas)

## Estrutura atual

```text
com.financecontrol
├── config/       RestAssuredConfig
├── clients/      AuthClient (login/register)
├── fixtures/     LoginFixture, UserRegistrationFixture
├── models/       LoginRequest, UserRegistrationRequest
└── tests/        AuthenticationTest, UserRegistrationTest
```

Pacote por responsabilidade (antes tudo em `authentication/`). Conceito aprendido: separar **como chamar** (client) de **o que validar** (teste) de **dados** (fixture/model).

## 1) RestAssuredConfig

- `RequestSpecification requestSpecification()` centraliza: `baseUri("http://localhost:8080")`, `contentType("application/json")`
- Conceitos: `given()`, `RequestSpecification`, reaproveitamento de template
- Aprendizado: separar configuração (host/headers) do teste (ação/validação)
- Sugestão futura (mínima): trocar string por `ContentType.JSON` (idiomático)

## 2) AuthClient

- Encapsula `given().spec(...).body(...).when().post(...)` para `/auth/login` e `/auth/register`
- Retorna `Response` (não valida nada) — validação fica no teste
- Aprendizado: client = transporte HTTP; teste = comportamento esperado

## 3) Fixtures e models

- `LoginFixture`: payloads válidos (username/email) para login seedado
- `UserRegistrationFixture`: `usuarioValido()` gera dados únicos (UUID + Data Faker); variantes para cada campo inválido; `prepararUsuariosDeDuplicidade()` garante usuário duplicado via `@BeforeAll`
- Models com campos privados + getters; `LoginRequest` usa `@JsonInclude(NON_NULL)` para omitir campos nulos (evita mandar `null` literal)
- Aprendizado: fixture resolve repetição real (mesmo payload montado 20+ vezes); factory de variações mantém teste focado no cenário

## 4) AuthenticationTest

- Cobertura: 15 casos (3x 200 — 1 por contrato/schema, 5x 401, 7x 400). Segue contrato PT-BR exato
- Helpers privados `validarErro400`/`validarErro401` para envelope comum (repetição real justificou)
- Uso: `.spec()` via client, JSONPath (`token`, `user.*`, `fields.*`), Hamcrest, `matchesJsonSchemaInClasspath`
- Aprendizado: `401` sem `fields`, `400` com `fields`; casos de borda (vazio, só espaços, maiúsculas)

## 5) UserRegistrationTest

- Cobertura: 27 testes (2x 201/contrato, 2x 409, 23x 400). `@ParameterizedTest` + `@ValueSource` para limites de tamanho
- `@BeforeAll` chama `prepararUsuariosDeDuplicidade()` (seed idempotente: 201 ou 409 esperado, senão falha com mensagem clara)
- Aprendizado: text blocks + `.formatted`, JSONPath aninhado, boundary testing, testes negativos completos (vazio/em branco/fora limite/inválido)

## 6) JSON Schemas

- `login-response-schema.json` e `user-register-response-schema.json` (draft-07)
- Validação de contrato com `matchesJsonSchemaInClasspath(...)` — garante estrutura (`token`, `user.id/name/email`) independente de valores
- Aprendizado: schema validation complementa matchers; contrato mudou → teste falha mesmo com valores ok

## Dependências novas (pom)

- `tools.jackson.core:jackson-databind` — serialização dos models para JSON
- `io.rest-assured:json-schema-validator` — validação de contrato
- `net.datafaker:datafaker` — geração de massa (nomes/emails)

Obs.: scopes `compile` (não `test`) — worth revisar para `test` já que só suíte usa.

## Conceitos Rest Assured aprendidos

- `given()` (pré-condições), `when()` (ação), `then()` (validações)
- `RequestSpecification` via `.spec()` (template reutilizável)
- `Response` vs `ValidatableResponse` (client retorna `response`, teste faz `.then()`)
- Serialização de objeto → JSON via `.body(objeto)`
- JSON Schema validation (`matchesJsonSchemaInClasspath`)
- Matchers Hamcrest: `equalTo`, `notNullValue`, `isEmptyOrNullString`
- JSONPath: campos simples/aninhados, `fields.<campo>`
- Parametrização com `@ParameterizedTest`/`@ValueSource`
- Fixtures + `@BeforeAll` para preparo de dados

## Oportunidades futuras (só quando fizer sentido)

- Reutilização de token (`Authorization`) — login retorna `token`, nenhum teste usa ainda
- Trocar `ContentType.JSON` idiomático no config
- Helpers de assert para envelopes comuns (só com muitos blocos iguais — hoje 2 helpers já cobrem)
- Escopos das dependências novas → `test`
- Clients para próximas features (accounts), quando existirem

## Conclusão

Estado atual: estrutura por responsabilidade (config/clients/fixtures/models/tests), contrato validado por schema, massa de teste isolada e única. Alinha com filosofia `guia.md` — abstrações introduzidas por repetição real (client, fixture), não por antecipação.

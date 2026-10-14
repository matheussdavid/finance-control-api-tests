# Revisão de estado atual — Rest Assured (aprendizado)

Data: 2026-10-13 (atualizado — regras confirmadas via sonda + schema de erro + register→login)

## Arquivos revisados

- `src/test/java/com/financecontrol/config/RestAssuredConfig.java` (15 linhas)
- `src/test/java/com/financecontrol/clients/authentication/AuthClient.java` (29 linhas)
- `src/test/java/com/financecontrol/fixtures/authentication/LoginFixture.java` (14 linhas)
- `src/test/java/com/financecontrol/fixtures/authentication/UserRegistrationFixture.java` (174 linhas)
- `src/test/java/com/financecontrol/models/authentication/LoginRequest.java`
- `src/test/java/com/financecontrol/models/authentication/UserRegistrationRequest.java`
- `src/test/java/com/financecontrol/tests/authentication/AuthenticationTest.java` (16 testes)
- `src/test/java/com/financecontrol/tests/authentication/UserRegistrationTest.java` (42 testes)
- `src/test/resources/schemas/authentication/*.json` (2 schemas)
- `src/test/resources/schemas/common/error-response-schema.json` (envelope de erro)

## Estrutura atual

```text
com.financecontrol
├── config/       RestAssuredConfig
├── clients/      AuthClient (login/register)
├── fixtures/     LoginFixture, UserRegistrationFixture
├── models/       LoginRequest, UserRegistrationRequest
├── assertions/   ErrorAssertions (400/401/409) + ErrorMessages (mensagens PT-BR)
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

- Cobertura: 16 casos (4x 200 — inclui register→login e2e, 5x 401, 7x 400). Segue contrato PT-BR exato
- Erros delegados a `ErrorAssertions.validarErro400/401` (compartilhado); multi-campo (`...SemInformarCredenciais`, `...IdentificadorESenhaVazios`) permanece inline no teste
- Uso: `.spec()` via client, JSONPath (`token`, `user.*`, `fields.*`), Hamcrest
- Aprendizado: `401` sem `fields`, `400` com `fields`; casos de borda (vazio, só espaços, maiúsculas); fluxo register→login com credenciais recém-cadastradas

## 5) UserRegistrationTest

- Cobertura: 42 testes (6x 201 — contrato, limites, charset/case/espaços; 3x 409; 33x 400). `@ParameterizedTest` + `@ValueSource` para limites de tamanho
- `@BeforeAll` chama `prepararUsuariosDeDuplicidade()` (seed idempotente: 201 ou 409 esperado, senão falha com mensagem clara)
- Erros de 1 campo via `ErrorAssertions.validarErro400(response, campo, MENSAGEM)`; 409 via `validarErro409`; `...ComBodyVazio` (multi-campo) inline
- Aprendizado: text blocks + `.formatted`, JSONPath aninhado, boundary testing, testes negativos completos (vazio/em branco/fora limite/inválido)

## 6) Regras confirmadas (sonda empírica na API)

Validado contra a API rodando (requests que falham em outro campo → sem efeito colateral; 1 cadastro controlado para normalização):

- E-mail com maiúsculas é **aceito e salvo literalmente** (sem lowercase); login é case-sensitive e usa match exato.
- E-mail com espaços nas extremidades → `400` `"O e-mail deve ser válido"` (valores **não** são trimados).
- Username **sem** restrição de padrão — caracteres especiais aceitos.
- Espaços em username/name são aceitos e **preservados** (sem trim): login exige a string com os espaços.

## 7) JSON Schemas

- `login-response-schema.json` e `user-register-response-schema.json` (draft-07) + `schemas/common/error-response-schema.json`
- Validação de contrato com `matchesJsonSchemaInClasspath(...)` — sucesso garante estrutura (`token`, `user.id/name/email`)
- Envelope de erro: `timestamp`, `status`, `error`, `message`, `path` obrigatórios na prática; `fields` (mapa `string→string`) só no `400`; schema exige só `status`/`error`/`message` (cobre 400/401/409)
- Contrato de erro validado por **1 caso** (`deveRespeitarContratoDeEnvelopeDeErro`, 400) — sem repetir em todo negativo (redundante)
- Aprendizado: schema validation complementa matchers; contrato mudou → teste falha mesmo com valores ok

## 8) Validações compartilhadas (assertions)

- `ErrorAssertions` (público): `validarErro400(response, campo, mensagem)`, `validarErro401(response)`, `validarErro409(response, mensagem)`; `validarEnvelope` privado retorna `ValidatableResponse` (encadeia `fields.<campo>` sem repetir o envelope)
- `ErrorMessages`: constantes PT-BR (mensagens); códigos (`VALIDATION_ERROR` etc.) `private` em `ErrorAssertions` (nenhum teste usa direto)
- Padrão: 1 campo → uma chamada; multi-campo → validação inline no teste (decisão explícita do projeto)
- Aprendizado: concentrar o contrato de erro num só lugar; trocar mensagem/regra em um único ponto

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
- Reuso de validação de contrato via assertions estáticas (`ErrorAssertions` + constantes)
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

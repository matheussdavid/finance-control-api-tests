# AGENTS.md

## What this repo is

API test automation suite only (`src/test`, no `src/main`). Java 21 + Rest Assured 6 + JUnit Jupiter 6 + Maven. Tests target an **external** finance-control API; the API source is not in this repo.

Read `guia.md` before proposing structural changes — it defines the project's evolution philosophy and rules (in Portuguese).

## Commands

```bash
mvn test                                                  # full suite
mvn test -Dtest=AuthenticationTest                        # one class
mvn test -Dtest='AuthenticationTest#deveRealizarLoginComCredenciaisValidas'   # one method
mvn test -Dtest=UserRegistrationTest -q
```

- No `mvnw` wrapper (`.mvn/` is empty) — use system `mvn` (3.9.x via sdkman).
- No lint, formatter, typecheck, codegen, or CI config exists. Only `mvn test` verifies.
- `maven.compiler.source/target=21`; installed JDK may be newer (25) — that's fine.

## Prerequisite: API must be running

Tests call `http://localhost:8080` (hardcoded in `src/test/java/com/financecontrol/config/RestAssuredConfig.java`). Suite fails with connection errors if the finance-control API isn't up. Check that before debugging test failures.

## Seeded / reserved data

Do not use these values in "happy path" tests — they already exist (tests rely on conflict):

- user `qatester` (username) → 409 on register
- email `qa@tester.com` → 409 on register
- login: `admin` / `12345678` (identifier accepts username or email, e.g. `admin@qa.com`)

Generate unique data instead — existing pattern: `gerarUsername()` / `gerarEmail()` using `Random.nextInt(100000)`.

## Error response contract (assert exact strings)

Success: `201` register (`token`, `user.*`), `200` login (`token`, `user.*`).

Failures share a fixed envelope — messages are PT-BR and must match **exactly**:

```json
{ "status": 400, "error": "VALIDATION_ERROR", "message": "Falha na validação", "fields": { "<field>": "<PT-BR msg>" } }
```

- `400` + `VALIDATION_ERROR` → field messages under `fields.<name>`
- `401` + `UNAUTHORIZED` → `"Usuário ou senha inválidos"` (no `fields`)
- `409` + `BUSINESS_RULE_VIOLATION` → business messages (e.g. `"Usuário já cadastrado"`, `"E-mail já cadastrado"`, `"As senhas não coincidem"`)
- Validation rules: name/username 3–50 chars (3–50 for username too), email max 50, password 8–30 chars; blank/whitespace-only counts as missing ("obrigatório").

## Conventions

- Tests live in `src/test/java/com/financecontrol/<feature>/` — one package per feature (`authentication`, future `accounts`, ...).
- `RestAssuredConfig.requestSpecification()` is the shared base spec (`baseUri` + JSON content type). Always `.spec(...)` it; don't set baseUri per test.
- Method naming: `deve<ExpectedBehavior>` in camelCase PT-BR (e.g. `deveRetornar400AoCadastrarUsuarioComEmailInvalido`).
- Endpoint paths as `private static final String X_URL` constants.
- Request bodies: Java text blocks with `.formatted(...)`.
- Assertions: Hamcrest matchers via `.statusCode(...)` + `.body(path, matcher)`.
- `@ParameterizedTest` + `@ValueSource` for input-boundary cases.
- No auth token handling yet — login/register return `token` but no test uses `Authorization` yet; introduce reuse only when a feature needs it.

## Workflow rules from `guia.md` (preserve)

- Treat current code as the source of truth; don't revert to previously replaced solutions.
- No abstraction without a concrete problem (no refactoring-for-refactoring); introduce clients/fixtures/factories when repetition actually shows up.
- Explain Rest Assured concepts behind each suggested change (`given/when/then`, `RequestSpecification`, matchers, etc.) — this is a learning project.
- Stay focused on Rest Assured/API tests; don't drift to Selenium/other tools unless asked.
- Response format when reviewing code: current state → problem → next step → why → code → future stage.

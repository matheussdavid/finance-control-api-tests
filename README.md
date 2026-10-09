# finance-control-api-tests

Suíte de automação de testes de API para a API finance-control. Projeto de aprendizado de **Rest Assured** com evolução gradual para estrutura profissional (ver `guia.md`).

> Apenas testes (`src/test`) — a fonte da API não está neste repositório.

## Stack

- Java 21
- Rest Assured 6 (+ `json-schema-validator`)
- JUnit Jupiter 6
- Jackson (serialização de payloads)
- Data Faker (massa de teste)
- Maven

## Pré-requisito

API finance-control rodando em `http://localhost:8080` (endereço fixo em `RestAssuredConfig`). Sem API, a suíte falha com erros de conexão.

## Como rodar

```bash
mvn test                                                      # suíte completa
mvn test -Dtest=AuthenticationTest                            # uma classe
mvn test -Dtest='AuthenticationTest#deveRealizarLoginComCredenciaisValidas'   # um método
```

Sem wrapper `mvnw` — usar `mvn` do sistema (3.9.x).

## Estrutura

```text
src/test/java/com/financecontrol/
├── config/        # RestAssuredConfig (baseUri + content type compartilhados)
├── clients/       # clientes HTTP (AuthClient: login/register)
├── fixtures/      # massa de teste (LoginFixture, UserRegistrationFixture)
├── models/        # payloads de request (LoginRequest, UserRegistrationRequest)
└── tests/         # testes por feature (tests/authentication/...)

src/test/resources/schemas/authentication/   # JSON Schemas das respostas
```

Separação de responsabilidade: **teste** = o que validar; **client** = como chamar a API; **fixture** = dados; **model** = payload.

## Cobertura atual

- `AuthenticationTest` — 15 testes de login (200 + contrato, 401, 400 com `fields`)
- `UserRegistrationTest` — 27 testes de cadastro (201 + contrato, 409, 400 por validação de campo), com `@ParameterizedTest` para limites de tamanho

## Dados reservados (não usar em happy path)

| Dado | Valor | Motivo |
|---|---|---|
| username | `qatester` | já existe → 409 |
| email | `qa@tester.com` | já existe → 409 |
| login | `admin` / `12345678` | usuário seedado (identifier aceita username ou email) |

Dados únicos: `UserRegistrationFixture.usuarioValido()` (UUID + Data Faker).

## Contrato de erro

Sucesso: `201` register, `200` login — ambos retornam `token` + `user`.

Falhas (mensagens PT-BR, correspondência exata):

- `400` + `VALIDATION_ERROR` → mensagens em `fields.<campo>`
- `401` + `UNAUTHORIZED` → `"Usuário ou senha inválidos"` (sem `fields`)
- `409` + `BUSINESS_RULE_VIOLATION` → ex.: `"Usuário já cadastrado"`, `"As senhas não coincidem"`

## Documentos

- `guia.md` — filosofia de evolução e regras do projeto (ler antes de mudar estrutura)
- `docs/revisao-estado-atual.md` — revisão do estado atual do código

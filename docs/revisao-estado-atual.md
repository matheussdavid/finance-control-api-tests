# Revisão de estado atual — Rest Assured (aprendizado)

Data: 2026-10-08

## Arquivos revisados

- `src/test/java/com/financecontrol/config/RestAssuredConfig.java` (14 linhas)
- `src/test/java/com/financecontrol/authentication/AuthenticationTest.java` (126 linhas)
- `src/test/java/com/financecontrol/authentication/UserRegistrationTest.java` (653 linhas)

## 1) RestAssuredConfig

- `RequestSpecification requestSpecification()` centraliza: `baseUri("http://localhost:8080")`, `contentType("application/json")`
- Conceitos: `given()`, `RequestSpecification`, reaproveitamento de template
- Aprendizado: separar configuração (host/headers) do teste (ação/validação)
- Sugestão futura (mínima): trocar string por `ContentType.JSON` (idiomático)

## 2) AuthenticationTest

- Cobertura: 6 casos (1x 200, 2x 401, 3x 400). Segue contrato PT-BR exato
- Uso: `.spec()`, `given/when/then`, JSONPath (`token`, `user.name`, `fields.*`), Hamcrest (`equalTo`, `notNullValue`)
- Aprendizado: estrutura clara preparo→ação→validação; `401` sem `fields`, `400` com `fields`

## 3) UserRegistrationTest

- Cobertura: 20 testes (201, 3x 409, 16x 400). Usa `@ParameterizedTest` + `@ValueSource`
- Dados: `gerarUsername()`/`gerarEmail()` com `Random.nextInt(100000)` (evita colisão com dados seedados `qatester`/`qa@tester.com`)
- Aprendizado: text blocks + `.formatted`, JSONPath aninhado, boundary testing, testes negativos completos (vazio/em branco/fora limite/inválido)

## Conceitos Rest Assured aprendidos

- `given()` (pré-condições), `when()` (ação), `then()` (validações)
- `RequestSpecification` via `.spec()` (template reutilizável)
- Matchers Hamcrest: `equalTo`, `notNullValue`
- JSONPath: campos simples/aninhados, `fields.<campo>`
- Parametrização com `@ParameterizedTest`/`@ValueSource`
- Text blocks para JSON legível

## Oportunidades futuras (só quando fizer sentido)

- API Clients (`AuthClient`, `UserClient`): separar "como chamar" de "o que validar"
- Test Data Factory: centralizar geração/construção de payloads (só com repetição real)
- Helpers de assert para envelopes comuns (só com muitos blocos iguais)
- Melhorar tipagem/uso de `ContentType.JSON`

## Conclusão

Estado atual é sólido: simples, legível, cobre contrato, sem complexidade prematura. Alinha com filosofia `guia.md` (evoluir quando houver problema real/oportunidade de aprendizado).

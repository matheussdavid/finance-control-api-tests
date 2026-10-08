# Contexto do projeto — Aprendizado e evolução com Rest Assured

Você está me ajudando a desenvolver e evoluir um projeto de automação de testes de API usando **Java + Rest Assured + JUnit 5**.

O objetivo principal deste projeto não é apenas criar uma suíte de testes funcionando. O objetivo é **aprender Rest Assured na prática e evoluir gradualmente para uma estrutura de automação de API próxima de projetos profissionais reais**.

## 1. Meu nível e objetivo

Tenho conhecimento básico de Java e estou aprendendo Rest Assured.

Quero aprender:

* Rest Assured;
* automação de testes de API;
* boas práticas de testes automatizados;
* organização de projetos de automação;
* padrões e abstrações usados em projetos reais;
* como estruturar uma suíte de testes que possa crescer sem ficar desorganizada.

Portanto, não quero que o projeto seja mantido artificialmente simples.

Ao mesmo tempo, não quero introduzir complexidade sem propósito.

A regra deve ser:

> **Começar pelo básico, mas evoluir para padrões e abstrações quando houver uma necessidade real ou quando aquele conceito representar um próximo passo importante de aprendizado.**

---

# 2. Filosofia de evolução do projeto

O projeto deve evoluir progressivamente.

Não devemos:

* evitar padrões apenas porque "o projeto é de aprendizado";
* evitar abstrações apenas porque deixam o código mais complexo;
* criar abstrações prematuramente apenas porque são consideradas boas práticas;
* refatorar repetidamente o mesmo código sem um motivo concreto;
* desfazer uma decisão já tomada sem apresentar uma razão técnica clara.

Devemos:

* observar o código atual;
* identificar problemas reais;
* explicar o problema;
* apresentar uma evolução coerente;
* implementar a mudança;
* continuar analisando o novo estado do projeto.

A evolução esperada é aproximadamente:

```text
Testes básicos
    ↓
Organização dos testes
    ↓
Configuração reutilizável
    ↓
Clients / abstração de endpoints
    ↓
Fixtures / geração de dados
    ↓
Autenticação reutilizável
    ↓
Validações mais organizadas
    ↓
Schemas / contratos
    ↓
Parametrização
    ↓
Estrutura mais próxima de um projeto profissional
    ↓
CI/CD e outras melhorias
```

Essa ordem não é rígida.

Se o código mostrar que determinado conceito deve ser introduzido antes, devemos fazer isso.

---

# 3. Regra importante: analisar o estado ATUAL

Sempre considere o código que eu apresentar como o **estado atual do projeto**.

Se fizermos uma refatoração e eu disser que implementei, essa refatoração passa a ser o novo ponto de partida.

Não volte automaticamente para uma solução anterior.

Não proponha novamente uma solução que acabamos de substituir, a menos que exista uma nova evidência de que a decisão anterior estava errada.

Quando uma abordagem tiver sido adotada, avalie:

* o que melhorou;
* quais problemas permanecem;
* qual é o próximo passo natural.

Não fique alternando entre soluções equivalentes.

---

# 4. Evitar "refatoração por refatoração"

Uma abstração deve ter uma justificativa.

Por exemplo, não quero transformar:

```java
int random = new Random().nextInt(100000);
String username = "qatester" + random;
String email = "qa" + random + "@test.com";
```

em uma função apenas para esconder exatamente o mesmo código:

```java
generateRandomUser();
```

se isso não trouxer nenhum benefício além de mover o código para outro lugar.

Por outro lado, se a geração de usuários começar a ser usada em muitos testes, podemos discutir uma evolução para algo como:

```text
Test Data Factory
Fixture
Builder
```

e usar isso como oportunidade de aprendizado.

A pergunta não deve ser apenas:

> "Podemos abstrair?"

Deve ser:

> "Qual problema estamos resolvendo com essa abstração e o que estou aprendendo ao introduzi-la?"

---

# 5. O projeto atual

O projeto é um portfólio de automação de testes de API.

Stack principal:

* Java
* Rest Assured
* JUnit 5
* Maven
* Git/GitHub

Também existe interesse em futuramente integrar:

* PostgreSQL;
* validações de banco;
* contratos/schema;
* CI com GitHub Actions;
* testes de frontend com Selenium;
* eventualmente outras ferramentas relacionadas a QA.

Mas o foco atual desta conversa é **Rest Assured e testes de API**.

Não desvie o foco para Selenium ou outras tecnologias sem que isso seja relevante para a questão atual.

---

# 6. Organização atual dos testes

Existe uma API com funcionalidades relacionadas a:

* cadastro de usuários;
* autenticação;
* contas financeiras.

Já existem diversos testes relacionados ao cadastro de usuários e autenticação.

A organização dos testes deve refletir o comportamento/feature da API.

Por exemplo:

```text
UserRegistration
Authentication
Accounts
```

A quantidade atual de testes não deve ser usada como argumento para criar uma arquitetura excessivamente complexa.

Primeiro analisamos a responsabilidade das classes e a repetição real.

---

# 7. Como quero aprender Rest Assured

Quando eu apresentar um código Rest Assured, não quero apenas receber uma versão "mais bonita".

Quero entender:

* o que o código atual está fazendo;
* qual recurso do Rest Assured está sendo utilizado;
* qual é a forma recomendada;
* por que a mudança é melhor;
* qual conceito estou aprendendo;
* se existe uma evolução natural para aquele código.

Por exemplo, se estivermos usando:

```java
given()
    .spec(...)
    .body(...)
.when()
    .post(...)
.then()
    .statusCode(201);
```

explique, quando relevante, o papel de:

* `given()`;
* `when()`;
* `then()`;
* `RequestSpecification`;
* `Response`;
* `ValidatableResponse`;
* serialização/desserialização;
* path parameters;
* query parameters;
* headers;
* cookies;
* autenticação;
* filtros;
* logging;
* matchers;
* schemas;
* reutilização de configuração.

Não é necessário explicar tudo sempre. Explique o conceito que estiver relacionado à mudança atual.

---

# 8. Evolução para API Clients

Uma possível evolução do projeto é encapsular chamadas HTTP em classes de client.

Por exemplo:

```java
public class AuthClient {

    public Response register(String body) {
        return given()
                .spec(RestAssuredConfig.requestSpecification())
                .body(body)
                .when()
                .post("/auth/register");
    }
}
```

Isso deve ser introduzido quando fizer sentido.

Ao introduzir esse padrão, explique que estamos separando:

```text
Teste
↓
comportamento que queremos validar

Client
↓
como fazemos a requisição HTTP
```

O objetivo não é apenas "deixar bonito", mas melhorar responsabilidade, reutilização e manutenção.

---

# 9. Test Data / Fixtures

Também podemos evoluir a geração de massa de teste.

Hoje alguns testes podem criar dados diretamente:

```java
String username = "...";
String email = "...";
```

Isso é aceitável enquanto ajuda no aprendizado inicial.

Conforme a repetição crescer, podemos evoluir para:

```text
TestDataFactory
Fixture
Builder
```

ou outra abordagem apropriada.

Não introduza um padrão complexo apenas por antecipação.

Mas também não evite esse padrão quando o projeto já estiver demonstrando claramente a necessidade dele.

---

# 10. Autenticação

A API possui autenticação.

Quero aprender progressivamente formas melhores de lidar com:

* login;
* token;
* headers de autorização;
* reutilização do token;
* setup de usuário autenticado;
* configuração de autenticação;
* eventualmente filtros/specifications ou mecanismos equivalentes do Rest Assured.

Se a implementação atual estiver repetindo autenticação em muitos testes, isso deve ser identificado como uma oportunidade de evolução.

---

# 11. Validações

Quero aprender diferentes formas de validar respostas:

```java
.statusCode(200)
.body("name", equalTo("Matheus"))
.body("active", equalTo(true))
```

e, conforme o projeto evoluir:

* validação de múltiplos campos;
* objetos aninhados;
* listas;
* JSONPath;
* matchers;
* headers;
* schemas;
* contratos;
* mensagens de erro;
* estruturas de erro;
* validações reutilizáveis quando houver necessidade.

Não transformar todas as validações em abstrações imediatamente.

Primeiro quero aprender a fazer a validação diretamente com Rest Assured.

Depois podemos discutir quando vale abstraí-la.

---

# 12. Banco de dados

O projeto também pode utilizar PostgreSQL para validações.

O banco não deve substituir a API como forma principal de criação de dados quando a própria API permite isso.

A ideia é aprender a usar o banco principalmente para:

* consultar dados;
* validar persistência;
* verificar integridade;
* confirmar efeitos de operações da API.

Se uma consulta ao banco puder ajudar a descobrir um ID ou estado necessário para o teste, isso pode ser utilizado.

Não quero usar SQL diretamente para criar massa quando a API já possui um endpoint adequado para isso, salvo quando houver uma razão específica.

---

# 13. Princípios de teste

Ao analisar os testes, considere:

* independência entre testes;
* massa de teste isolada;
* dados únicos quando necessário;
* previsibilidade;
* legibilidade;
* manutenção;
* testes positivos e negativos;
* regras de negócio;
* códigos HTTP;
* contratos da API;
* mensagens de erro;
* validação de estrutura;
* cenários de borda.

Também quero aprender a identificar quando um teste está realmente validando um comportamento e quando ele está apenas repetindo implementação.

---

# 14. Não assumir que a solução atual está correta

Mesmo que eu já tenha implementado algo, você deve continuar avaliando criticamente.

Se perceber que:

* uma abstração ficou ruim;
* uma responsabilidade está no lugar errado;
* existe duplicação;
* um padrão foi aplicado prematuramente;
* uma solução pode evoluir;
* existe uma prática profissional que ainda não estamos aplicando;

explique isso.

Mas não proponha mudanças apenas por estética.

---

# 15. Como responder às minhas perguntas

Quando eu apresentar um trecho de código, prefira esta estrutura:

### 1. O que temos hoje

Explique brevemente o que o código atual faz.

### 2. Problema ou oportunidade

Explique se existe realmente algo que merece mudança.

Se não existir, diga isso.

### 3. Próximo passo

Sugira a evolução que faz mais sentido **neste momento do projeto**.

### 4. Por que

Explique o conceito de Rest Assured/Java/automação que estamos aprendendo.

### 5. Código

Mostre a implementação sugerida.

### 6. Próximo estágio

Se houver uma evolução futura relevante, apenas mencione-a.

Não tente implementar cinco níveis de abstração de uma vez.

---

# 16. Regra contra alucinação de contexto

Não assuma que voltamos ao estado anterior do projeto.

Se eu disser:

> "feito"

considere que aquela mudança foi aplicada.

Se houver dúvida sobre como o código está agora, peça o código atual antes de sugerir uma nova refatoração.

Não invente classes, métodos ou estruturas que não foram mostrados.

Não presuma que uma refatoração existe apenas porque seria uma boa prática.

---

# 17. Objetivo final

O objetivo deste projeto é que, ao longo da evolução, eu consiga olhar para o código e entender:

* como testar APIs com Rest Assured;
* como estruturar testes;
* como separar responsabilidades;
* quando criar um API Client;
* quando criar fixtures;
* como trabalhar com autenticação;
* como validar respostas;
* como validar contratos;
* como trabalhar com banco;
* como organizar massa de testes;
* como evitar duplicação;
* como aplicar padrões sem criar complexidade desnecessária;
* e como reconhecer quando um projeto de automação precisa evoluir estruturalmente.

Quero sair do projeto não apenas sabendo escrever:

```java
given()
    .when()
    .then();
```

mas sabendo **construir e evoluir uma suíte de testes de API profissionalmente**.

## Regra principal

> **Não simplifique o projeto apenas para evitar complexidade.**
>
> **Não adicione complexidade apenas para parecer profissional.**
>
> **Introduza cada conceito no momento em que ele fizer sentido para o código e, principalmente, quando ele representar uma oportunidade real de aprendizado.**

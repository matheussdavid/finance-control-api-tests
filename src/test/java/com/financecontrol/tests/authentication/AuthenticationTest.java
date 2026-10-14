package com.financecontrol.tests.authentication;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.fixtures.authentication.LoginFixture;
import com.financecontrol.fixtures.authentication.UserRegistrationFixture;
import com.financecontrol.models.authentication.LoginRequest;
import com.financecontrol.models.authentication.UserRegistrationRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.financecontrol.assertions.ErrorAssertions.validarErro400;
import static com.financecontrol.assertions.ErrorAssertions.validarErro401;
import static com.financecontrol.assertions.ErrorMessages.FALHA_VALIDACAO;
import static com.financecontrol.assertions.ErrorMessages.IDENTIFICADOR_OBRIGATORIO;
import static com.financecontrol.assertions.ErrorMessages.SENHA_OBRIGATORIA;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class AuthenticationTest {

    @Test
    void deveRespeitarContratoAoRealizarLoginComCredenciaisValidas() {
        LoginRequest login = LoginFixture.loginValidoComUsername();
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/authentication/login-response-schema.json"));
    }

    @Test
    void deveRealizarLoginComUsuarioESenhaValidos() {
        LoginRequest login = LoginFixture.loginValidoComUsername();
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .body("user.id", not(isEmptyOrNullString()))
                .body("user.name", equalTo("Tester"))
                .body("user.email", equalTo("admin@qa.com"));
    }

    @Test
    void deveRealizarLoginComEmailESenhaValidos() {
        LoginRequest login = LoginFixture.loginValidoComEmail();
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .body("user.id", not(isEmptyOrNullString()))
                .body("user.name", equalTo("Tester"))
                .body("user.email", equalTo("admin@qa.com"));
    }

    @Test
    void deveRealizarLoginComCredenciaisDeUsuarioRecemCadastrado() {
        UserRegistrationRequest cadastro = UserRegistrationFixture.usuarioValido();
        new AuthClient().register(cadastro);

        LoginRequest login = new LoginRequest(
                cadastro.getUsername(),
                cadastro.getPassword()
        );
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .body("user.id", not(isEmptyOrNullString()))
                .body("user.name", equalTo(cadastro.getName()))
                .body("user.email", equalTo(cadastro.getEmail()));
    }

    @Test
    void deveRetornar401AoRealizarLoginComEmailESenhaInvalida() {
        LoginRequest login = new LoginRequest("admin@qa.com", "senha-incorreta");
        Response response = new AuthClient().login(login);

        validarErro401(response);
    }

    @Test
    void deveRetornar401AoRealizarLoginComUsuarioESenhaInvalida() {
        LoginRequest login = new LoginRequest("admin", "senha-incorreta");
        Response response = new AuthClient().login(login);

        validarErro401(response);
    }

    @Test
    void deveRetornar401AoRealizarLoginComEmailInexistente() {
        LoginRequest login = new LoginRequest("email.inexistente@qa.com", "qualquer-senha");
        Response response = new AuthClient().login(login);

        validarErro401(response);
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarIdentificador() {
        String body = """
                { "password": "qualquer-senha" }
                """;

        Response response = new AuthClient().login(body);

        validarErro400(response, "identifier", IDENTIFICADOR_OBRIGATORIO);
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarSenha() {
        String body = """
                { "identifier": "admin" }
                """;

        Response response = new AuthClient().login(body);

        validarErro400(response, "password", SENHA_OBRIGATORIA);
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarCredenciais() {
        Response response = new AuthClient().login("{}");

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo(FALHA_VALIDACAO))
                .body("fields.identifier", equalTo(IDENTIFICADOR_OBRIGATORIO))
                .body("fields.password", equalTo(SENHA_OBRIGATORIA));
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorNulo() {
        LoginRequest login = LoginFixture.loginComIdentificadorNulo();
        Response response = new AuthClient().login(login);

        validarErro400(response, "identifier", IDENTIFICADOR_OBRIGATORIO);
    }

    @Test
    void deveRetornar400AoRealizarLoginComSenhaNula() {
        LoginRequest login = LoginFixture.loginComSenhaNula();
        Response response = new AuthClient().login(login);

        validarErro400(response, "password", SENHA_OBRIGATORIA);
    }

    @Test
    void deveRetornar400AoRealizarLoginComCredenciaisNulas() {
        LoginRequest login = LoginFixture.loginComCredenciaisNulas();
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo(FALHA_VALIDACAO))
                .body("fields.identifier", equalTo(IDENTIFICADOR_OBRIGATORIO))
                .body("fields.password", equalTo(SENHA_OBRIGATORIA));
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorVazio() {
        LoginRequest login = new LoginRequest("", "12345678");
        Response response = new AuthClient().login(login);

        validarErro400(response, "identifier", IDENTIFICADOR_OBRIGATORIO);
    }

    @Test
    void deveRetornar400AoRealizarLoginComSenhaVazia() {
        LoginRequest login = new LoginRequest("admin", "");
        Response response = new AuthClient().login(login);

        validarErro400(response, "password", SENHA_OBRIGATORIA);
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorESenhaVazios() {
        LoginRequest login = new LoginRequest("", "");
        Response response = new AuthClient().login(login);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo(FALHA_VALIDACAO))
                .body("fields.identifier", equalTo(IDENTIFICADOR_OBRIGATORIO))
                .body("fields.password", equalTo(SENHA_OBRIGATORIA));
    }

    @Test
    void deveRetornar401AoRealizarLoginComUsuarioEmMaiusculas() {
        LoginRequest login = new LoginRequest("ADMIN", "12345678");
        Response response = new AuthClient().login(login);

        validarErro401(response);
    }

    @Test
    void deveRetornar401AoRealizarLoginComEmailEmMaiusculas() {
        LoginRequest login = new LoginRequest("ADMIN@QA.COM", "12345678");
        Response response = new AuthClient().login(login);

        validarErro401(response);
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorContendoApenasEspacos() {
        LoginRequest login = new LoginRequest("   ", "12345678");
        Response response = new AuthClient().login(login);

        validarErro400(response, "identifier", IDENTIFICADOR_OBRIGATORIO);
    }
}

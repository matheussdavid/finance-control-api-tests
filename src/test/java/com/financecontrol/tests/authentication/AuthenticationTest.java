package com.financecontrol.tests.authentication;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.fixtures.authentication.LoginFixture;
import com.financecontrol.models.authentication.LoginRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class AuthenticationTest {

    private void validarErro400(Response response) {
        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"));
    }

    private void validarErro401(Response response) {
        response.then()
                .statusCode(401)
                .body("status", equalTo(401))
                .body("error", equalTo("UNAUTHORIZED"))
                .body("message", equalTo("Usuário ou senha inválidos"));
    }

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
    void deveRetornar400AoRealizarLoginSemInformarEmail() {
        LoginRequest login = new LoginRequest(null, "qualquer-senha");
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarSenha() {
        LoginRequest login = new LoginRequest("admin", null);
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarCredenciais() {
        LoginRequest login = new LoginRequest(null, null);
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"))
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorVazio() {
        LoginRequest login = new LoginRequest("", "12345678");
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoRealizarLoginComSenhaVazia() {
        LoginRequest login = new LoginRequest("admin", "");
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoRealizarLoginComIdentificadorESenhaVazios() {
        LoginRequest login = new LoginRequest("", "");
        Response response = new AuthClient().login(login);

        validarErro400(response);
        response.then()
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"))
                .body("fields.password", equalTo("A senha é obrigatória"));
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

        validarErro400(response);
        response.then()
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"));
    }
}

package com.financecontrol.authentication;

import com.financecontrol.config.RestAssuredConfig;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationTest {

    private static final String LOGIN_URL = "/auth/login";

    @Test
    void deveRealizarLoginComCredenciaisValidas() {
        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                    {
                      "identifier": "admin",
                      "password": "12345678"
                    }
                  """)
        .when()
                .post(LOGIN_URL)
        .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("user.name", equalTo("Tester"))
                .body("user.email", equalTo("admin@qa.com"));
    }

    @Test
    void deveRetornar401AoRealizarLoginComSenhaInvalida() {

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
                "identifier": "admin@qa.com",
                "password": "senha-incorreta"
            }
            """)
        .when()
                .post(LOGIN_URL)
        .then()
                .statusCode(401)
                .body("status", equalTo(401))
                .body("error", equalTo("UNAUTHORIZED"))
                .body("message", equalTo("Usuário ou senha inválidos"));
    }

    @Test
    void deveRetornar401AoRealizarLoginComEmailInexistente() {

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
                "identifier": "email.inexistente@qa.com",
                "password": "qualquer-senha"
            }
            """)
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(401)
                .body("status", equalTo(401))
                .body("error", equalTo("UNAUTHORIZED"))
                .body("message", equalTo("Usuário ou senha inválidos"));
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarEmail() {
        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
                "password": "senha123"
            }
            """)
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarSenha() {
        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                        {
                          "identifier": "admina"
                        }
                """)
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoRealizarLoginSemInformarCredenciais() {
        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("{}")
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.identifier", equalTo("Usuário ou e-mail é obrigatório"))
                .body("fields.password", equalTo("A senha é obrigatória"));
    }
}

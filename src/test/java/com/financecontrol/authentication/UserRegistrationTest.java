package com.financecontrol.authentication;

import com.financecontrol.config.RestAssuredConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserRegistrationTest {

    private static final String REGISTER_URL = "/auth/register";

    private String gerarUsername() {
        return "qatester" + new Random().nextInt(100000);
    }

    private String gerarEmail() {
        return "qa" + new Random().nextInt(100000) + "@test.com";
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComDadosValidos() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                    {
                      "name": "QA Tester",
                      "username": "%s",
                      "email": "%s",
                      "password": "12345678",
                      "confirmPassword": "12345678"
                    }
                    """.formatted(username, email))
        .when()
                .post(REGISTER_URL)
        .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo("QA Tester"))
                .body("user.email", equalTo(email));
    }

    @Test
    void deveRetornar409AoCadastrarUsuarioComUsuarioJaCadastrado() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "qatester",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("BUSINESS_RULE_VIOLATION"))
                .body("message", equalTo("Usuário já cadastrado"));
    }

    @Test
    void deveRetornar409AoCadastrarUsuarioComEmailJaCadastrado() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "qa@tester.com",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("BUSINESS_RULE_VIOLATION"))
                .body("message", equalTo("E-mail já cadastrado"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarNome() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarUsername() {
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarEmail() {
        String username = gerarUsername();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarSenha() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "%s",
                  "confirmPassword": "12345678"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarConfirmacaoDeSenha() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.confirmPassword", equalTo("A confirmação de senha é obrigatória"));
    }

    @Test
    void deveRetornar409AoCadastrarUsuarioComSenhasDiferentes() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "87654321"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("BUSINESS_RULE_VIOLATION"))
                .body("message", equalTo("As senhas não coincidem"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567",
            "1234567890123456789012345678901"
    })
    void deveRetornar400AoCadastrarUsuarioComSenhaForaDoLimite(String password) {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "%s",
              "password": "%s",
              "confirmPassword": "12345678"
            }
            """.formatted(username, email, password))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.password", equalTo("A senha deve ter entre 8 e 30 caracteres"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567",
            "1234567890123456789012345678901"
    })
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaForaDoLimite(String confirmPassword) {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "%s",
              "password": "12345678",
              "confirmPassword": "%s"
            }
            """.formatted(username, email, confirmPassword))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.confirmPassword", equalTo("A senha deve ter entre 8 e 30 caracteres"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailInvalido() {
        String username = gerarUsername();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "email-invalido",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail deve ser válido"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "QA",
            "vmfCcyE2Tdgw4kvQsgMat4rUopN3L9298ZCvQbHAldbjbc7p1mte76ie9NJZTXAQ"
    })
    void deveRetornar400AoCadastrarUsuarioComUsernameForaDoLimite(String username) {
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.username", equalTo("O usuário deve ter entre 3 e 50 caracteres"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "QA",
            "vmfCcyE2Tdgw4kvQsgMat4rUopN3L9298ZCvQbHAldbjbc7p1mte76ie9NJZTXAQ"
    })
    void deveRetornar400AoCadastrarUsuarioComNomeForaDoLimite(String name) {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "%s",
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(name, username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome deve ter entre 3 e 50 caracteres"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailMaiorQue50Caracteres() {
        String username = gerarUsername();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "usuario123456789012345678901234567890123456789@test.com",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail deve ter no máximo 50 caracteres"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComNameVazio() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "",
                  "username": "%s",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameVazio() {
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "",
                  "email": "%s",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailVazio() {
        String username = gerarUsername();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
                {
                  "name": "QA Tester",
                  "username": "%s",
                  "email": "",
                  "password": "12345678",
                  "confirmPassword": "12345678"
                }
                """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComBodyVazio() {
        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("{}")
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome é obrigatório"))
                .body("fields.confirmPassword", equalTo("A confirmação de senha é obrigatória"))
                .body("fields.password", equalTo("A senha é obrigatória"))
                .body("fields.username", equalTo("O usuário é obrigatório"))
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComNomeEmBranco() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "   ",
              "username": "%s",
              "email": "%s",
              "password": "12345678",
              "confirmPassword": "12345678"
            }
            """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameEmBranco() {
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "   ",
              "email": "%s",
              "password": "12345678",
              "confirmPassword": "12345678"
            }
            """.formatted(email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailEmBranco() {
        String username = gerarUsername();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "   ",
              "password": "12345678",
              "confirmPassword": "12345678"
            }
            """.formatted(username))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaEmBranco() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "%s",
              "password": "   ",
              "confirmPassword": "12345678"
            }
            """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaVazia() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "%s",
              "password": "",
              "confirmPassword": "12345678"
            }
            """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaEmBranco() {
        String username = gerarUsername();
        String email = gerarEmail();

        given()
                .spec(RestAssuredConfig.requestSpecification())
                .body("""
            {
              "name": "QA Tester",
              "username": "%s",
              "email": "%s",
              "password": "12345678",
              "confirmPassword": "   "
            }
            """.formatted(username, email))
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(400)
                .body("fields.confirmPassword",
                        equalTo("A confirmação de senha é obrigatória"));
    }



}

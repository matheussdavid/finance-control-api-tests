package com.financecontrol.tests.authentication;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.fixtures.authentication.UserRegistrationFixture;
import com.financecontrol.models.authentication.UserRegistrationRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserRegistrationTest {

    @BeforeAll
    static void prepararDadosDeTeste() {
        UserRegistrationFixture.prepararUsuariosDeDuplicidade();
    }

    @Test
    void deveRespeitarContratoAoRealizarRegistroDeUsuarioComCredenciaisValidas() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        Response response = new AuthClient()
                .register(usuario);

        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/authentication/user-register-response-schema.json"));
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComDadosValidos() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        Response response = new AuthClient()
                .register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo(usuario.getName()))
                .body("user.email", equalTo(usuario.getEmail()));
    }

    @Test
    void deveRetornar409AoCadastrarUsuarioComUsuarioJaCadastrado() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsernameDuplicado();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("BUSINESS_RULE_VIOLATION"))
                .body("message", equalTo("Usuário já cadastrado"));
    }

    @Test
    void deveRetornar409AoCadastrarUsuarioComEmailJaCadastrado() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmailDuplicado();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("BUSINESS_RULE_VIOLATION"))
                .body("message", equalTo("E-mail já cadastrado"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarNome() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        String body = """
            {
              "username": "%s",
              "email": "%s",
              "password": "%s",
              "confirmPassword": "%s"
            }
            """.formatted(
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );

        Response response = new AuthClient().register(body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarUsername() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        String body = """
            {
              "name": "%s",
              "email": "%s",
              "password": "%s",
              "confirmPassword": "%s"
            }
            """.formatted(
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );

        Response response = new AuthClient().register(body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarEmail() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        String body = """
            {
              "name": "%s",
              "username": "%s",
              "password": "%s",
              "confirmPassword": "%s"
            }
            """.formatted(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );

        Response response = new AuthClient().register(body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarSenha() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        String body = """
            {
              "name": "%s",
              "username": "%s",
              "email": "%s",
              "confirmPassword": "%s"
            }
            """.formatted(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getConfirmPassword()
        );

        Response response = new AuthClient().register(body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioSemInformarConfirmacaoDeSenha() {
        UserRegistrationRequest usuario = UserRegistrationFixture.usuarioValido();

        String body = """
            {
              "name": "%s",
              "username": "%s",
              "email": "%s",
              "password": "%s"
            }
            """.formatted(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword()
        );

        Response response = new AuthClient().register(body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.confirmPassword", equalTo("A confirmação de senha é obrigatória"));
    }


    @Test
    void deveRetornar409AoCadastrarUsuarioComSenhasDiferentes() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenhasDiferentes();

        Response response = new AuthClient().register(usuario);

        response.then()
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
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha(password);

        Response response = new AuthClient().register(usuario);

        response.then()
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
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha(confirmPassword);

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.confirmPassword", equalTo("A senha deve ter entre 8 e 30 caracteres"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailInvalido() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmailInvalido();

        Response response = new AuthClient().register(usuario);

        response.then()
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
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername(username);

        Response response = new AuthClient().register(usuario);

        response.then()
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
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome(name);

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome deve ter entre 3 e 50 caracteres"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailMaiorQue50Caracteres() {
        String email = "usuario123456789012345678901234567890123456789@test.com";

        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail(email);

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail deve ter no máximo 50 caracteres"));
    }


    @Test
    void deveRetornar400AoCadastrarUsuarioComNameVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome("");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername("");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail("");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComBodyVazio() {
        Response response = new AuthClient().register("{}");

        response.then()
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
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome("   ");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.name", equalTo("O nome é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername("   ");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.username", equalTo("O usuário é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail("   ");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.email", equalTo("O e-mail é obrigatório"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha("   ");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaVazia() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha("");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.password", equalTo("A senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha("   ");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.confirmPassword", equalTo("A confirmação de senha é obrigatória"));
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaVazia() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha("");

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(400)
                .body("fields.confirmPassword",
                        equalTo("A confirmação de senha é obrigatória"));
    }



}

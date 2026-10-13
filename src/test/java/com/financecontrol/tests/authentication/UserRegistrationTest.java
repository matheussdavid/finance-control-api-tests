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

    private void validarErro400(Response response, String campo, String mensagem) {
        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Falha na validação"))
                .body("fields." + campo, equalTo(mensagem));
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

        validarErro400(response, "name", "O nome é obrigatório");
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

        validarErro400(response, "username", "O usuário é obrigatório");
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

        validarErro400(response, "email", "O e-mail é obrigatório");
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

        validarErro400(response, "password", "A senha é obrigatória");
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

        validarErro400(response, "confirmPassword", "A confirmação de senha é obrigatória");
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
    @ValueSource(ints = {7,31})
    void deveRetornar400AoCadastrarUsuarioComSenhaForaDoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "password", "A senha deve ter entre 8 e 30 caracteres");
    }

    @ParameterizedTest
    @ValueSource(ints = {8,30})
    void deveRetornar201AoCadastrarUsuarioComSenhaNoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenhaIguais(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo(usuario.getName()))
                .body("user.email", equalTo(usuario.getEmail()));
    }

    @ParameterizedTest
    @ValueSource(ints = {7,31})
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaForaDoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "confirmPassword", "A senha deve ter entre 8 e 30 caracteres");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailInvalido() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmailInvalido();

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail deve ser válido");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailComEspacosNasExtremidades() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmailComEspacos();

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail deve ser válido");
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComUsernameComCaracteresEspeciais() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsernameComCaracteresEspeciais();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo(usuario.getName()))
                .body("user.email", equalTo(usuario.getEmail()));
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComEmailEmMaiusculas() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmailEmMaiusculas();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.email", equalTo(usuario.getEmail()));
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComUsernameComEspacosNasExtremidades() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsernameComEspacos();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue());
    }

    @Test
    void deveRetornar201AoCadastrarUsuarioComNomeComEspacosNasExtremidades() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNomeComEspacos();

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.name", equalTo(usuario.getName()));
    }

    @Test
    void deveRespeitarContratoDeEnvelopeDeErro() {
        Response response = new AuthClient().register("{}");

        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/common/error-response-schema.json"));
    }

    @ParameterizedTest
    @ValueSource(ints = {2,51})
    void deveRetornar400AoCadastrarUsuarioComUsernameForaDoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "username", "O usuário deve ter entre 3 e 50 caracteres");
    }

    @ParameterizedTest
    @ValueSource(ints = {3,50})
    void deveRetornar201AoCadastrarUsuarioComUsernameNoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo(usuario.getName()))
                .body("user.email", equalTo(usuario.getEmail()));
    }

    @ParameterizedTest
    @ValueSource(ints = {2,51})
    void deveRetornar400AoCadastrarUsuarioComNomeForaDoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "name", "O nome deve ter entre 3 e 50 caracteres");
    }

    @ParameterizedTest
    @ValueSource(ints = {3,50})
    void deveRetornar201AoCadastrarUsuarioComNomeNoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome(
                        UserRegistrationFixture.gerarTexto(tamanho));

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue())
                .body("user.name", equalTo(usuario.getName()));
    }

    @ParameterizedTest
    @ValueSource(ints = {51})
    void deveRetornar400AoCadastrarUsuarioComEmailMaiorQue50Caracteres(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail(
                        UserRegistrationFixture.gerarEmailComTamanho(tamanho));

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail deve ter no máximo 50 caracteres");
    }

    @ParameterizedTest
    @ValueSource(ints = {50})
    void deveRetornar201AoCadastrarUsuarioComEmailNoLimite(int tamanho) {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail(
                        UserRegistrationFixture.gerarEmailComTamanho(tamanho));

        Response response = new AuthClient().register(usuario);

        response.then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("user.id", notNullValue());
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComNameVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome("");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "name", "O nome é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername("");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "username", "O usuário é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailVazio() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail("");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail é obrigatório");
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

        validarErro400(response, "name", "O nome é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername("   ");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "username", "O usuário é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail("   ");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha("   ");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "password", "A senha é obrigatória");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaVazia() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha("");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "password", "A senha é obrigatória");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaEmBranco() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha("   ");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "confirmPassword", "A confirmação de senha é obrigatória");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaVazia() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha("");

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "confirmPassword", "A confirmação de senha é obrigatória");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComNomeNulo() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComNome(null);

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "name", "O nome é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComUsernameNulo() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComUsername(null);

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "username", "O usuário é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComEmailNulo() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComEmail(null);

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "email", "O e-mail é obrigatório");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComSenhaNula() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComSenha(null);

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "password", "A senha é obrigatória");
    }

    @Test
    void deveRetornar400AoCadastrarUsuarioComConfirmacaoDeSenhaNula() {
        UserRegistrationRequest usuario =
                UserRegistrationFixture.usuarioComConfirmacaoDeSenha(null);

        Response response = new AuthClient().register(usuario);

        validarErro400(response, "confirmPassword", "A confirmação de senha é obrigatória");
    }
}

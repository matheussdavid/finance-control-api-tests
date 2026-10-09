package com.financecontrol.fixtures.authentication;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.models.authentication.UserRegistrationRequest;
import io.restassured.response.Response;
import net.datafaker.Faker;

import java.util.UUID;

public class UserRegistrationFixture {

    private static final Faker FAKER = new Faker();

    private static final String SENHA_PADRAO = "Teste@123";
    private static final String USERNAME_DUPLICIDADE = "qatester";
    private static final String EMAIL_DUPLICIDADE = "qa@tester.com";

    public static UserRegistrationRequest usuarioValido() {
        String identificador = UUID.randomUUID()
                .toString()
                .replace("-", "");

        String name = FAKER.name().fullName();
        String username = "qa" + identificador;
        String email = "qa" + identificador + "@test.com";

        return new UserRegistrationRequest(
                name,
                username,
                email,
                SENHA_PADRAO,
                SENHA_PADRAO
        );
    }

    public static UserRegistrationRequest usuarioComSenhasDiferentes() {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                "12345678",
                "87654321"
        );
    }

    public static UserRegistrationRequest usuarioComUsername(String username) {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                username,
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );
    }

    public static UserRegistrationRequest usuarioComNome(String name) {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                name,
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );
    }

    public static UserRegistrationRequest usuarioComEmail(String email) {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                usuario.getUsername(),
                email,
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );
    }

    public static UserRegistrationRequest usuarioComSenha(String password) {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                password,
                usuario.getConfirmPassword()
        );
    }

    public static UserRegistrationRequest usuarioComConfirmacaoDeSenha(String confirmPassword) {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                confirmPassword
        );
    }

    public static UserRegistrationRequest usuarioComEmailInvalido() {
        UserRegistrationRequest usuario = usuarioValido();

        return new UserRegistrationRequest(
                usuario.getName(),
                usuario.getUsername(),
                "email-invalido",
                usuario.getPassword(),
                usuario.getConfirmPassword()
        );
    }

    public static UserRegistrationRequest usuarioComUsernameDuplicado() {
        return new UserRegistrationRequest(
                "QA Tester",
                USERNAME_DUPLICIDADE,
                "qa." + java.util.UUID.randomUUID().toString().replace("-", "")
                        + "@test.com",
                "12345678",
                "12345678"
        );
    }

    public static UserRegistrationRequest usuarioComEmailDuplicado() {
        return new UserRegistrationRequest(
                "QA Tester",
                "qa" + java.util.UUID.randomUUID().toString().replace("-", ""),
                EMAIL_DUPLICIDADE,
                "12345678",
                "12345678"
        );
    }


    public static void prepararUsuariosDeDuplicidade() {
        AuthClient client = new AuthClient();

        UserRegistrationRequest usuario = new UserRegistrationRequest(
                "QA Tester",
                USERNAME_DUPLICIDADE,
                EMAIL_DUPLICIDADE,
                "12345678",
                "12345678"
        );

        Response response = client.register(usuario);

        if (response.statusCode() == 201) {
            return;
        }

        if (response.statusCode() == 409) {
            String mensagem = response.jsonPath().getString("message");

            if ("Usuário já cadastrado".equals(mensagem)
                    || "E-mail já cadastrado".equals(mensagem)) {
                return;
            }
        }

        throw new IllegalStateException(
                "Não foi possível preparar os usuários de duplicidade. "
                        + "Status: " + response.statusCode()
                        + ", resposta: " + response.asString()
        );
    }
}

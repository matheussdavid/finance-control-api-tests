package com.financecontrol.fixtures.authentication;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.models.authentication.UserRegistrationRequest;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public final class UserRegistrationFixture {

    private static final Faker FAKER = new Faker();

    private static final String SENHA_PADRAO = "Teste@123";
    private static final String USERNAME_DUPLICIDADE = "qatester";
    private static final String EMAIL_DUPLICIDADE = "qa@tester.com";
    private static final String DOMINIO_EMAIL = "@test.com";

    private UserRegistrationFixture() {
    }

    public static String gerarTexto(int tamanho) {
        return RandomStringUtils.insecure().nextAlphanumeric(tamanho);
    }

    public static String gerarEmailComTamanho(int tamanho) {
        int tamanhoLocal = tamanho - DOMINIO_EMAIL.length();

        if (tamanhoLocal < 1) {
            throw new IllegalArgumentException(
                    "Tamanho de e-mail deve ser maior que o domínio: "
                            + DOMINIO_EMAIL.length());
        }

        return gerarTexto(tamanhoLocal) + DOMINIO_EMAIL;
    }

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
        return usuarioValido().withPassword("12345678").withConfirmPassword("87654321");
    }

    public static UserRegistrationRequest usuarioComUsername(String username) {
        return usuarioValido().withUsername(username);
    }

    public static UserRegistrationRequest usuarioComNome(String name) {
        return usuarioValido().withName(name);
    }

    public static UserRegistrationRequest usuarioComEmail(String email) {
        return usuarioValido().withEmail(email);
    }

    public static UserRegistrationRequest usuarioComSenha(String password) {
        return usuarioValido().withPassword(password);
    }

    public static UserRegistrationRequest usuarioComSenhaIguais(String senha) {
        return usuarioValido().withPassword(senha).withConfirmPassword(senha);
    }

    public static UserRegistrationRequest usuarioComConfirmacaoDeSenha(String confirmPassword) {
        return usuarioValido().withConfirmPassword(confirmPassword);
    }

    public static UserRegistrationRequest usuarioComEmailInvalido() {
        return usuarioValido().withEmail("email-invalido");
    }

    public static UserRegistrationRequest usuarioComUsernameComCaracteresEspeciais() {
        return usuarioValido().withUsername(
                "qa!@#$%^&*" + UUID.randomUUID().toString().replace("-", ""));
    }

    public static UserRegistrationRequest usuarioComEmailEmMaiusculas() {
        UserRegistrationRequest usuario = usuarioValido();
        return usuario.withEmail(usuario.getEmail().toUpperCase());
    }

    public static UserRegistrationRequest usuarioComEmailComEspacos() {
        UserRegistrationRequest usuario = usuarioValido();
        return usuario.withEmail(" " + usuario.getEmail() + " ");
    }

    public static UserRegistrationRequest usuarioComUsernameComEspacos() {
        UserRegistrationRequest usuario = usuarioValido();
        return usuario.withUsername(" " + usuario.getUsername() + " ");
    }

    public static UserRegistrationRequest usuarioComNomeComEspacos() {
        UserRegistrationRequest usuario = usuarioValido();
        return usuario.withName(" " + usuario.getName() + " ");
    }

    public static UserRegistrationRequest usuarioComUsernameDuplicado() {
        return usuarioValido()
                .withName("QA Tester")
                .withUsername(USERNAME_DUPLICIDADE)
                .withEmail("qa." + java.util.UUID.randomUUID().toString().replace("-", "")
                        + "@test.com");
    }

    public static UserRegistrationRequest usuarioComEmailDuplicado() {
        return usuarioValido()
                .withName("QA Tester")
                .withUsername("qa" + java.util.UUID.randomUUID().toString().replace("-", ""))
                .withEmail(EMAIL_DUPLICIDADE);
    }

    public static void prepararUsuariosDeDuplicidade() {
        Response response = new AuthClient().register(
                new UserRegistrationRequest(
                        "QA Tester",
                        USERNAME_DUPLICIDADE,
                        EMAIL_DUPLICIDADE,
                        SENHA_PADRAO,
                        SENHA_PADRAO
                )
        );

        if (response.statusCode() == 201 || response.statusCode() == 409) {
            return;
        }

        throw new IllegalStateException(
                "Falha ao preparar usuários de duplicidade. Status: "
                        + response.statusCode()
                        + ", resposta: " + response.asString()
        );
    }
}

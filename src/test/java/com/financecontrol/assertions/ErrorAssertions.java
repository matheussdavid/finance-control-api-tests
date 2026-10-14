package com.financecontrol.assertions;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static com.financecontrol.assertions.ErrorMessages.CREDENCIAIS_INVALIDAS;
import static com.financecontrol.assertions.ErrorMessages.FALHA_VALIDACAO;
import static org.hamcrest.Matchers.equalTo;

public final class ErrorAssertions {

    private static final String ERRO_VALIDACAO = "VALIDATION_ERROR";
    private static final String ERRO_NAO_AUTORIZADO = "UNAUTHORIZED";
    private static final String ERRO_REGRA_NEGOCIO = "BUSINESS_RULE_VIOLATION";

    private ErrorAssertions() {}

    public static void validarErro400(Response response, String campo, String mensagem) {
        validarEnvelope(response, 400, ERRO_VALIDACAO, FALHA_VALIDACAO)
                .body("fields." + campo, equalTo(mensagem));
    }

    public static void validarErro401(Response response) {
        validarEnvelope(response, 401, ERRO_NAO_AUTORIZADO, CREDENCIAIS_INVALIDAS);
    }

    public static void validarErro409(Response response, String mensagem) {
        validarEnvelope(response, 409, ERRO_REGRA_NEGOCIO, mensagem);
    }

    private static ValidatableResponse validarEnvelope(
            Response response, int status, String error, String message) {
        return response.then()
                .statusCode(status)
                .body("status", equalTo(status))
                .body("error", equalTo(error))
                .body("message", equalTo(message));
    }
}

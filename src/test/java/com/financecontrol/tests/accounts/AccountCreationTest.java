package com.financecontrol.tests.accounts;

import com.financecontrol.clients.authentication.AuthClient;
import com.financecontrol.config.RestAssuredConfig;
import com.financecontrol.fixtures.authentication.LoginFixture;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AccountCreationTest {

    private static final String ACCOUNTS_URL = "/accounts";

    @Test
    void deveCriarContaComDadosValidos() {
        String token = new AuthClient()
                .login(LoginFixture.loginValidoComUsername())
                .jsonPath()
                .getString("token");

        String body = """
                {
                  "name": "Nubank",
                  "type": "CHECKING",
                  "initialBalance": 1000
                }
                """;

        Response response = given()
                .spec(RestAssuredConfig.requestSpecification())
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post(ACCOUNTS_URL);

        response.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Nubank"))
                .body("type", equalTo("CHECKING"))
                .body("initialBalance", equalTo(1000))
                .body("balance", equalTo(1000))
                .body("status", equalTo("ACTIVE"));
    }
}

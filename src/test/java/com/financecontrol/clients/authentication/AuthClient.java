package com.financecontrol.clients.authentication;

import com.financecontrol.config.RestAssuredConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    private static final String LOGIN_URL = "/auth/login";
    private static final String REGISTER_URL = "/auth/register";

    public Response login(Object body) {
        return given()
                .spec(RestAssuredConfig.requestSpecification())
                .body(body)
                .when()
                .post(LOGIN_URL);
    }

    public Response register(Object body) {
        return given()
                .spec(RestAssuredConfig.requestSpecification())
                .body(body)
                .when()
                .post(REGISTER_URL);
    }

}

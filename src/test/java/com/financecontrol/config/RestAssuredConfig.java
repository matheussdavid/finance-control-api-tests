package com.financecontrol.config;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestAssuredConfig {

    public static RequestSpecification requestSpecification() {
        return given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON);
    }
}

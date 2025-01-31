package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.core.container.LocalStackTestContainer;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class HealthTest implements LocalStackTestContainer {

    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath("/health");
    }

    @Test
    public void healthEndpointExposed() {
        given()
            .spec(requestSpecification)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode());
    }
}
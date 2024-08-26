package com.blitzar.cards.management.web.controller;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import com.blitzar.cards.management.factory.CardTestFactory;
import com.blitzar.cards.management.web.LocalStackTestContainer;
import io.micronaut.context.MessageSource;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CancelCardControllerIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private RequestSpecification requestSpecification;

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(ManagementCardAPIConstants.MANAGEMENT_CARD_V1_MAPPING);
    }

    @Test
    public void givenExistentCardId_whenCancelCard_thenReturnOk(){
        var card = CardTestFactory.buildCard(bankAccountId, cardholderName);
        dynamoDbTable.putItem(card);

        given()
            .spec(requestSpecification)
                .pathParam("card-id", card.getCardId())
        .when()
            .patch("/cancellation")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        var updatedCard = dynamoDbTable.getItem(card);
        assertThat(updatedCard.getCardStatus()).isEqualTo(CardStatus.CANCELLED);
    }

    @Test
    public void givenNonExistentCardId_whenCancelCard_thenReturnNotFound(){
        var nonExistentCardId = UUID.randomUUID().toString();

        given()
            .spec(requestSpecification)
        .when()
            .patch("/cancellation", nonExistentCardId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.notFound", Locale.getDefault(), nonExistentCardId).orElseThrow()));
    }
}
package com.blitzar.web.graphql;

import com.blitzar.LocalStackTestContainer;
import com.blitzar.cards.domain.Card;
import com.blitzar.factory.CardTestFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardFetcherIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    private RequestSpecification requestSpecification;

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath("/graphql");
    }

    @Test
    public void givenExistentCardId_whenActivateCard_thenReturnOk() {
        var card = CardTestFactory.buildCard(bankAccountId, cardholderName);
        dynamoDbTable.putItem(card);

        String graphQLQuery = """
                { "query": "{ card(cardId:\\"%s\\") {cardId, bankAccountId, cardholderName, cardNumber, cardStatus}}" }"""
                .formatted(card.getCardId());
        given()
            .spec(requestSpecification)
            .body(graphQLQuery)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .and()
            .rootPath("data")
                .body("card", notNullValue())
                    .body("card.bankAccountId", equalTo(card.getBankAccountId()))
                    .body("card.cardholderName", equalTo(card.getCardholderName()))
                    .body("card.cardNumber", equalTo(card.getCardNumber()))
                    .body("card.cardStatus", equalTo(card.getCardStatus().toString()));
    }
}

package com.jcondotta.cards.query.service;

import com.jcondotta.cards.core.container.LocalStackTestContainer;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.CardTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
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
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardFetcherIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    private RequestSpecification requestSpecification;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

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
        var card = CardTestFactory.buildCard(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
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
                    .body("card.bankAccountId", equalTo(card.getBankAccountId().toString()))
                    .body("card.cardholderName", equalTo(card.getCardholderName()))
                    .body("card.cardNumber", equalTo(card.getCardNumber()))
                    .body("card.cardStatus", equalTo(card.getCardStatus().toString()));
    }
}

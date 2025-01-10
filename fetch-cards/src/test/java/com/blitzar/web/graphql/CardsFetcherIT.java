package com.blitzar.web.graphql;

import com.blitzar.cards.container.LocalStackTestContainer;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.factory.CardTestFactory;
import com.blitzar.cards.helper.TestBankAccount;
import com.blitzar.cards.helper.TestCardholder;
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
import static org.hamcrest.Matchers.hasSize;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardsFetcherIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    private RequestSpecification requestSpecification;


    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final UUID BANK_ACCOUNT_ID_ITALY = TestBankAccount.ITALY.getBankAccountId();

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
        Card card1 = CardTestFactory.buildCard(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
        Card card2 = CardTestFactory.buildCard(BANK_ACCOUNT_ID_ITALY, CARDHOLDER_JEFFERSON);

        dynamoDbTable.putItem(card1);
        dynamoDbTable.putItem(card2);

        String query = """
                { "query": "{ cards(bankAccountId:\\"%s\\") {cardId, bankAccountId, cardholderName, cardNumber, cardStatus}}" }"""
                .formatted(BANK_ACCOUNT_ID_BRAZIL.toString());

        given()
            .spec(requestSpecification)
            .body(query)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .and()
            .rootPath("data")
                .body("cards", hasSize(1))
                .body("cards[0].cardId", equalTo(card1.getCardId().toString()))
                .body("cards[0].bankAccountId", equalTo(card1.getBankAccountId().toString()))
                .body("cards[0].cardholderName", equalTo(card1.getCardholderName()))
                .body("cards[0].cardNumber", equalTo(card1.getCardNumber()))
                .body("cards[0].cardStatus", equalTo(card1.getCardStatus().toString()));
    }
}

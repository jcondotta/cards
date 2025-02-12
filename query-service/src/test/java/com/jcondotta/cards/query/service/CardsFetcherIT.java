package com.jcondotta.cards.query.service;

import com.jcondotta.cards.core.container.LocalStackTestContainer;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.CardTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CardsCacheService;
import com.jcondotta.cards.core.service.dto.CardsDTO;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardsFetcherIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private CardsCacheService<CardsDTO> cardsCacheService;

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
    public void givenExistentCardId_whenFetchCard_thenReturnOk() {
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

        var cacheKey = new BankAccountIdCacheKey(BANK_ACCOUNT_ID_BRAZIL);
        var cacheEntryValue = cardsCacheService.getCacheEntryValue(cacheKey);

        assertThat(cacheEntryValue)
                .isPresent()
                .get()
                .satisfies(cardsDTO -> {
                    // Validate count
                    assertThat(cardsDTO.count()).isEqualTo(1);
                    assertThat(cardsDTO.lastEvaluatedKey()).isNull();

                    assertThat(cardsDTO.cards())
                            .hasSize(1)
                            .first()
                            .satisfies(cardDTO -> {
                                assertThat(cardDTO.cardId()).isEqualTo(card1.getCardId());
                                assertThat(cardDTO.bankAccountId()).isEqualTo(card1.getBankAccountId());
                                assertThat(cardDTO.cardholderName()).isEqualTo(card1.getCardholderName());
                                assertThat(cardDTO.cardNumber()).isEqualTo(card1.getCardNumber());
                                assertThat(cardDTO.cardStatus()).isEqualTo(card1.getCardStatus());
                                assertThat(cardDTO.dailyWithdrawalLimit()).isEqualTo(card1.getDailyWithdrawalLimit());
                            });
                });
    }
}

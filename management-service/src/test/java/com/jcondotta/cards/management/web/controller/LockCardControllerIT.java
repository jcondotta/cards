package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.core.container.LocalStackTestContainer;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.factory.CardTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
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
class LockCardControllerIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

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
                .basePath(CardApiPaths.BY_ID);
    }

    @Test
    public void shouldLockCard_whenCardIdExists() {
        var card = CardTestFactory.buildCard(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
        card.setCardStatus(CardStatus.ACTIVE);
        dynamoDbTable.putItem(card);

        given()
            .spec(requestSpecification)
            .pathParam("card-id", card.getCardId())
        .when()
            .patch("/lock")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        var updatedCard = dynamoDbTable.getItem(card);
        assertThat(updatedCard.getCardStatus()).isEqualTo(CardStatus.LOCKED);
    }

    @Test
    public void shouldReturnNotFound_whenCardIdDoesNotExist() {
        var nonExistentCardId = UUID.randomUUID();

        given()
            .spec(requestSpecification)
            .pathParam("card-id", nonExistentCardId)
        .when()
            .patch("/lock")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.notFound", Locale.getDefault(), nonExistentCardId)
                        .orElseThrow()));
    }
}

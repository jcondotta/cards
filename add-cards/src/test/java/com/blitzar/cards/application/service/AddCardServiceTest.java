package com.blitzar.cards.application.service;

import com.blitzar.cards.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.cards.argument_provider.security.ThreatInputArgumentProvider;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.factory.ClockTestFactory;
import com.blitzar.cards.factory.ValidatorTestFactory;
import com.blitzar.cards.helper.TestBankAccount;
import com.blitzar.cards.helper.TestCardholder;
import com.blitzar.cards.web.events.request.AddCardRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddCardServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;
    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    private AddCardService addCardService;

    @Mock
    private DynamoDbTable<Card> dynamoDbTable;

    @BeforeEach
    void beforeEach(){
        addCardService = new AddCardService(dynamoDbTable, TEST_CLOCK_FIXED_INSTANT, VALIDATOR);
    }

    @Test
    void shouldSaveCard_whenRequestIsValid(){
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        addCardService.addCard(addCardRequest);
        verify(dynamoDbTable).putItem(cardCaptor.capture());

        Card card = cardCaptor.getValue();

        assertAll(
                () -> assertThat(card.getCardId()).isNotNull(),
                () -> assertThat(card.getBankAccountId()).isEqualTo(addCardRequest.bankAccountId()),
                () -> assertThat(card.getCardholderName()).isEqualTo(addCardRequest.cardholderName()),
                () -> assertThat(card.getCardNumber()).isNotNull(),
                () -> assertThat(card.getCardStatus()).isEqualTo(AddCardRequest.DEFAULT_CARD_STATUS),
                () -> assertThat(card.getDailyWithdrawalLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT),
                () -> assertThat(card.getDailyPaymentLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT),
                () -> assertThat(card.getCreatedAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT)),
                () -> assertThat(card.getExpirationDate()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT)
                        .plus(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE, ChronoUnit.YEARS))
        );

        verify(dynamoDbTable).putItem(any(Card.class));
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull(){
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenCardholderNameIsBlank(String invalidCardholderName){
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenCardholderNameIsMalicious(String invalidCardholderName) {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }

    @Test
    void shouldThrowConstraintViolationException_whenCardholderNameExceeds21Characters(){
        var veryLongCardholderName = RandomStringUtils.randomAlphabetic(22);
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.length.limit");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }
}

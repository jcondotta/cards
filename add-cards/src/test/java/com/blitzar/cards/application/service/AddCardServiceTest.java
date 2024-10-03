package com.blitzar.cards.application.service;

import com.blitzar.cards.application.ValidatorTestBuilder;
import com.blitzar.cards.application.helper.TestBankAccount;
import com.blitzar.cards.arguments.InvalidStringArgumentProvider;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.factory.ClockTestFactory;
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

    private AddCardService addCardService;

    @Mock
    private DynamoDbTable<Card> dynamoDbTable;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    private static final Clock CLOCK = ClockTestFactory.testClockFixedInstant;
    private static final Validator VALIDATOR = ValidatorTestBuilder.getValidator();

    @BeforeEach
    public void beforeEach(){
        addCardService = new AddCardService(dynamoDbTable, CLOCK, VALIDATOR);
    }

    @Test
    public void shouldSaveCard_whenRequestIsValid(){
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
                () -> assertThat(card.getCreatedAt()).isEqualTo(LocalDateTime.now(CLOCK)),
                () -> assertThat(card.getExpirationDate()).isEqualTo(LocalDateTime.now(CLOCK)
                        .plus(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE, ChronoUnit.YEARS))
        );

        verify(dynamoDbTable).putItem(any(Card.class));
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNull(){
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        var violation = exception.getConstraintViolations().stream()
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull"),
                () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("bankAccountId")
        );

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenCardholderNameIsInvalid(String invalidCardholderName){
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        var violation = exception.getConstraintViolations().stream()
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank"),
                () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName")
        );

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }

    @Test
    public void shouldThrowConstraintViolationException_whenCardholderNameExceeds21Characters(){
        var invalidCardholderName = RandomStringUtils.randomAlphabetic(22);
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("card.cardholderName.length.limit"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName")
                ));

        verify(dynamoDbTable, never()).putItem(any(Card.class));
    }
}

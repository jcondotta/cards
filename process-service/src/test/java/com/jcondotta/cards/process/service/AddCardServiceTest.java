package com.jcondotta.cards.process.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.ClockTestFactory;
import com.jcondotta.cards.core.factory.ValidatorTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddCardServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_NAME_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

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
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_NAME_JEFFERSON);

        Card card = addCardService.addCard(addCardRequest);

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
                        .plusYears(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE))
        );

        verify(dynamoDbTable).putItem(any(Card.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull(){
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_NAME_JEFFERSON);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowConstraintViolationException_whenCardholderNameExceeds31Characters(){
        var veryLongCardholderName = RandomStringUtils.randomAlphabetic(32);
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(dynamoDbTable);
    }
}

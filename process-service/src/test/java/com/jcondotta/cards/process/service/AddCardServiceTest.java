package com.jcondotta.cards.process.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.ClockTestFactory;
import com.jcondotta.cards.core.factory.ValidatorTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CacheEvictionService;
import com.jcondotta.cards.core.service.dto.CardDTO;
import com.jcondotta.cards.core.service.dto.CardsDTO;
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

    @Mock
    private CacheEvictionService<CardsDTO> cacheEvictionService;

    @BeforeEach
    void beforeEach(){
        addCardService = new AddCardService(dynamoDbTable, cacheEvictionService, TEST_CLOCK_FIXED_INSTANT, VALIDATOR);
    }

    @Test
    void shouldSaveCard_whenRequestIsValid(){
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_NAME_JEFFERSON);

        CardDTO cardDTO = addCardService.addCard(addCardRequest);

        assertAll(
                () -> assertThat(cardDTO.cardId()).isNotNull(),
                () -> assertThat(cardDTO.bankAccountId()).isEqualTo(addCardRequest.bankAccountId()),
                () -> assertThat(cardDTO.cardholderName()).isEqualTo(addCardRequest.cardholderName()),
                () -> assertThat(cardDTO.cardNumber()).isNotNull(),
                () -> assertThat(cardDTO.cardStatus()).isEqualTo(AddCardRequest.DEFAULT_CARD_STATUS),
                () -> assertThat(cardDTO.dailyWithdrawalLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT),
                () -> assertThat(cardDTO.dailyPaymentLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT),
                () -> assertThat(cardDTO.createdAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT)),
                () -> assertThat(cardDTO.expirationDate()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT)
                        .plusYears(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE))
        );

        verify(dynamoDbTable).putItem(any(Card.class));
        verify(cacheEvictionService).evictCacheEntry(eq(new BankAccountIdCacheKey(addCardRequest.bankAccountId())));
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull(){
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_NAME_JEFFERSON);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(dynamoDbTable);
        verifyNoInteractions(cacheEvictionService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenCardholderNameExceeds31Characters(){
        var veryLongCardholderName = RandomStringUtils.randomAlphabetic(32);
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongCardholderName);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(dynamoDbTable);
        verifyNoInteractions(cacheEvictionService);
    }
}

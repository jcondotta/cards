package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.cards.core.factory.ValidatorTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.management.web.CardApplicationEventProducer;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddCardServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @InjectMocks
    private AddCardService addCardService;

    @Mock
    private CardApplicationEventProducer eventProducer;

    @BeforeEach
    public void beforeEach(){
        addCardService = new AddCardService(eventProducer, VALIDATOR);
    }

    @Test
    public void shouldSendSQSMessage_whenAddCardRequestIsValid() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);

        addCardService.addCard(addCardRequest);

//TODO        verify(eventProducer).send(addCardRequest, anyString());
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));

        assertAll(
                () -> assertThat(exception.getConstraintViolations()).hasSize(1),
                () -> assertThat(exception.getConstraintViolations().iterator().next().getMessage()).isEqualTo("card.bankAccountId.notNull")
        );

        verify(eventProducer, never()).send(addCardRequest);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenCardholderNameIsBlank(String invalidCardholderName){
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var exception = assertThrows(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));

        assertAll(
                () -> assertThat(exception.getConstraintViolations()).hasSize(1),
                () -> assertThat(exception.getConstraintViolations().iterator().next().getMessage()).isEqualTo("card.cardholderName.notBlank")
        );

        verify(eventProducer, never()).send(addCardRequest);
    }

    @Test
    public void shouldThrowConstraintViolationException_whenCardholderNameExceeds31Characters() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(32));

        var exception = assertThrows(ConstraintViolationException.class, () -> addCardService.addCard(addCardRequest));

        assertAll(
                () -> assertThat(exception.getConstraintViolations()).hasSize(1),
                () -> assertThat(exception.getConstraintViolations().iterator().next().getMessage()).isEqualTo("card.cardholderName.tooLong")
        );

        verify(eventProducer, never()).send(addCardRequest);
    }
}

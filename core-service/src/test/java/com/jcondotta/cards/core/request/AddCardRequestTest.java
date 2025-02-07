package com.jcondotta.cards.core.request;

import com.jcondotta.cards.core.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.cards.core.factory.ValidatorTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddCardRequestTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_NAME_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Test
    void shouldNotDetectConstraintViolation_whenRequestIsValid() {
        var addCardholderRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_NAME_JEFFERSON);

        var constraintViolations = VALIDATOR.validate(addCardholderRequest);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void shouldDetectConstraintViolation_whenBankAccountIdIsNull() {
        var addCardholderRequest = new AddCardRequest(null, CARDHOLDER_NAME_JEFFERSON);

        var constraintViolations = VALIDATOR.validate(addCardholderRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldDetectConstraintViolation_whenCardholderNameIsBlank(String invalidCardholderName) {
        var addCardholderRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        var constraintViolations = VALIDATOR.validate(addCardholderRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenCardholderNameIsTooLong() {
        final var veryLongCardholderName = "J".repeat(32);
        var addCardholderRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongCardholderName);

        var constraintViolations = VALIDATOR.validate(addCardholderRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });
    }

    @Test
    void shouldDetectMultipleConstraintViolation_whenAllFieldsAreNull() {
        var addCardholderRequest = new AddCardRequest(null, null);

        var constraintViolations = VALIDATOR.validate(addCardholderRequest);
        assertThat(constraintViolations)
                .hasSize(2)
                .anySatisfy(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                })
                .anySatisfy(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });
    }
}
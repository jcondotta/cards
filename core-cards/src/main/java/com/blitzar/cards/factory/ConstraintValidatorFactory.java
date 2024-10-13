package com.blitzar.cards.factory;

import com.blitzar.cards.validation.annotation.CardholderName;
import com.blitzar.cards.validation.annotation.validator.CardholderNameValidator;
import io.micronaut.context.annotation.Factory;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;

@Factory
public class ConstraintValidatorFactory {

    @Singleton
    ConstraintValidator<CardholderName, String> cardholderNameValidator() {
        return (value, annotationMetadata, context) -> new CardholderNameValidator().isValid(value, context);
    }
}

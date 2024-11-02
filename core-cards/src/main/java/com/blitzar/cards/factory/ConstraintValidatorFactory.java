package com.blitzar.cards.factory;

import com.blitzar.cards.validation.annotation.SecureInput;
import com.blitzar.cards.validation.annotation.validator.SecureInputValidator;
import io.micronaut.context.annotation.Factory;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;

@Factory
public class ConstraintValidatorFactory {

    @Singleton
    ConstraintValidator<SecureInput, String> secureInputValidator(SecureInputValidator secureInputValidator) {
        return (value, annotationMetadata, context) -> secureInputValidator.isValid(value, context);
    }
}

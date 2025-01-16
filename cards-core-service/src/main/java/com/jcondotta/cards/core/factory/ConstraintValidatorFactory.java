package com.jcondotta.cards.core.factory;

import com.jcondotta.cards.core.validation.annotation.SecureInput;
import com.jcondotta.cards.core.validation.annotation.validator.SecureInputValidator;
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

package com.blitzar.cards.validation.annotation.validator;

import com.blitzar.cards.validation.annotation.CardholderName;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;

public class CardholderNameValidator implements ConstraintValidator<CardholderName, String> {

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<CardholderName> annotationMetadata, io.micronaut.validation.validator.constraints.@NonNull ConstraintValidatorContext context) {
        return true;
    }
}

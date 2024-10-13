package com.blitzar.cards.validation.annotation;

import com.blitzar.cards.validation.annotation.validator.CardholderNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "card.cardholderName.notBlank")
@Size(max = 21, message = "card.cardholderName.length.limit")
@SecureInput(message = "card.cardholderName.invalid")
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardholderNameValidator.class )
public @interface CardholderName {

    String message() default "Invalid cardholder name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


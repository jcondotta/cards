package com.blitzar.cards.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank(message = "card.cardholderName.notBlank")
@Size(max = 21, message = "card.cardholderName.length.limit")
@Target(value = { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(value = RUNTIME)
@Constraint(validatedBy = {})
public @interface CardholderName {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

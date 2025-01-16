package com.jcondotta.cards.core.validation.annotation;

import com.jcondotta.cards.core.validation.annotation.validator.SecureInputValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { SecureInputValidator.class })
@Documented
public @interface SecureInput {
    String message() default "Invalid input, potential malicious code detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
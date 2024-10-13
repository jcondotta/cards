package com.blitzar.cards.validation.annotation.validator;

import com.blitzar.cards.validation.annotation.SecureInput;
import com.blitzar.cards.validation.security.ThreatInputPatternDetector;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import software.amazon.awssdk.utils.StringUtils;

public class SecureInputValidator implements ConstraintValidator<SecureInput, String> {

    private final ThreatInputPatternDetector threatInputPatternDetector;

    public SecureInputValidator() {
        this.threatInputPatternDetector = new ThreatInputPatternDetector();
    }

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<SecureInput> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        // If the input is blank or null, it's considered valid
        if (StringUtils.isBlank(value)) {
            return true;
        }

        boolean containsThreatInputPattern = threatInputPatternDetector.containsAnyPattern(value);

        if (containsThreatInputPattern) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

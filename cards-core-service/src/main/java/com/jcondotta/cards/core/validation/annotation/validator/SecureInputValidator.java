package com.jcondotta.cards.core.validation.annotation.validator;

import com.jcondotta.cards.core.validation.annotation.SecureInput;
import com.jcondotta.cards.core.validation.security.ThreatInputPatternDetector;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import software.amazon.awssdk.utils.StringUtils;

@Singleton
public class SecureInputValidator implements ConstraintValidator<SecureInput, String> {

    private final ThreatInputPatternDetector threatInputPatternDetector;

    public SecureInputValidator() {
        this.threatInputPatternDetector = new ThreatInputPatternDetector();
    }

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<SecureInput> annotationMetadata, @NonNull ConstraintValidatorContext context) {
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

package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.management.web.CardApplicationEventProducer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AddCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddCardService.class);
    private final CardApplicationEventProducer eventProducer;
    private final Validator validator;

    @Inject
    public AddCardService(CardApplicationEventProducer eventProducer, Validator validator) {
        this.eventProducer = eventProducer;
        this.validator = validator;
    }

    public void addCard(AddCardRequest request) {
        LOGGER.info("Validating card application request",
                StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                StructuredArguments.keyValue("cardholderName", request.cardholderName())
        );

        var constraintViolations = validator.validate(request);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("Validation failed. Violations: {}",
                    constraintViolations,
                    StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                    StructuredArguments.keyValue("cardholderName", request.cardholderName())
            );
            throw new ConstraintViolationException(constraintViolations);
        }

        LOGGER.info("Validation passed. Sending card application request to SQS queue",
                StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                StructuredArguments.keyValue("cardholderName", request.cardholderName())
        );

        eventProducer.send(request);

        LOGGER.info("Successfully sent card application request to SQS",
                StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                StructuredArguments.keyValue("cardholderName", request.cardholderName())
        );
    }
}

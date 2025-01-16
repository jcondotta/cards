package com.jcondotta.cards.management.service;

import com.jcondotta.cards.management.web.CardApplicationEventProducer;
import com.jcondotta.cards.core.request.AddCardRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AddCardService {

    private static final Logger logger = LoggerFactory.getLogger(AddCardService.class);
    private final CardApplicationEventProducer eventProducer;
    private final Validator validator;

    @Inject
    public AddCardService(CardApplicationEventProducer eventProducer, Validator validator) {
        this.eventProducer = eventProducer;
        this.validator = validator;
    }

    public void addCard(AddCardRequest request) {
        logger.info("[BankAccountId={}, CardholderName={}] Validating card application request",
                request.bankAccountId(), request.cardholderName());

        var constraintViolations = validator.validate(request);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}, CardholderName={}] Validation failed. Violations: {}",
                    request.bankAccountId(), request.cardholderName(), constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        logger.info("[BankAccountId={}, CardholderName={}] Validation passed. Sending card application request to SQS queue",
                request.bankAccountId(), request.cardholderName());

        eventProducer.send(request);

        logger.info("[BankAccountId={}, CardholderName={}] Successfully sent card application request to SQS",
                request.bankAccountId(), request.cardholderName());
    }
}

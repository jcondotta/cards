package com.jcondotta.cards.add.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.request.AddCardRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Singleton
public class AddCardService {

    private static final Logger logger = LoggerFactory.getLogger(AddCardService.class);

    private final DynamoDbTable<Card> dynamoDbTable;
    private final Clock currentInstant;
    private final Validator validator;

    @Inject
    public AddCardService(DynamoDbTable<Card> dynamoDbTable, Clock currentInstant, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public Card addCard(AddCardRequest request){
        logger.info("[BankAccountId={}, CardholderName={}] Attempting to add a new card", request.bankAccountId(), request.cardholderName());

        var constraintViolations = validator.validate(request);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}, CardholderName={}] Validation errors for request. Violations: {}",
                    request.bankAccountId(), request.cardholderName(), constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        var card = new Card();
        card.setCardId(UUID.randomUUID());
        card.setBankAccountId(request.bankAccountId());
        card.setCardholderName(request.cardholderName());

        var cardNumber = new Faker().finance().creditCard();
        card.setCardNumber(cardNumber);
        card.setCardStatus(AddCardRequest.DEFAULT_CARD_STATUS);
        card.setDailyWithdrawalLimit(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT);
        card.setDailyPaymentLimit(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT);
        card.setCreatedAt(LocalDateTime.now(currentInstant));
        card.setExpirationDate(LocalDateTime.now(currentInstant)
                .plusYears(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE));

        dynamoDbTable.putItem(card);

        logger.info("[BankAccountId={}, CardholderName={}] Card saved to DB", request.bankAccountId(), request.cardholderName());
        logger.debug("Saved Card: {}", card);

        return card;
    }
}
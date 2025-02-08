package com.jcondotta.cards.process.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CacheEvictionService;
import com.jcondotta.cards.core.service.dto.CardDTO;
import com.jcondotta.cards.core.service.dto.CardsDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import net.datafaker.Faker;
import net.logstash.logback.argument.StructuredArguments;
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
    private final CacheEvictionService<CardsDTO> cacheEvictionService;
    private final Clock currentInstant;
    private final Validator validator;

    @Inject
    public AddCardService(DynamoDbTable<Card> dynamoDbTable,
                          CacheEvictionService<CardsDTO> cacheEvictionService,
                          Clock currentInstant,
                          Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.cacheEvictionService = cacheEvictionService;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public CardDTO addCard(AddCardRequest request) {
        logger.info("Attempting to add a new card",
                StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                StructuredArguments.keyValue("cardholderName", request.cardholderName())
        );

        var constraintViolations = validator.validate(request);
        if (!constraintViolations.isEmpty()) {
            logger.warn("Validation errors for request. Violations: {}",
                    constraintViolations,
                    StructuredArguments.keyValue("bankAccountId", request.bankAccountId()),
                    StructuredArguments.keyValue("cardholderName", request.cardholderName())
            );
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

        logger.info("Card saved to DB",
                StructuredArguments.keyValue("cardId", card.getCardId()),
                StructuredArguments.keyValue("bankAccountId", card.getBankAccountId()),
                StructuredArguments.keyValue("cardholderName", card.getCardholderName())
        );

        cacheEvictionService.evictCacheEntry(new BankAccountIdCacheKey(request.bankAccountId()));

        return new CardDTO(card);
    }
}

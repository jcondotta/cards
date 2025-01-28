package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.exception.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class LockCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockCardService.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public LockCardService(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void lockCard(@NotNull UUID cardId) {
        LOGGER.info("Request received to lock the card",
                StructuredArguments.keyValue("cardId", cardId)
        );

        var key = Key.builder().partitionValue(cardId.toString()).build();
        var card = dynamoDbTable.getItem(key);

        if (Objects.isNull(card)) {
            LOGGER.warn("Card not found, locking request failed",
                    StructuredArguments.keyValue("cardId", cardId)
            );
            throw new ResourceNotFoundException("card.notFound", cardId);
        }

        LOGGER.info("Card found. Locking the card",
                StructuredArguments.keyValue("cardId", cardId)
        );

        card.setCardStatus(CardStatus.LOCKED);
        dynamoDbTable.putItem(card);

        LOGGER.info("Successfully locked the card",
                StructuredArguments.keyValue("cardId", cardId)
        );
    }
}

package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.exception.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class CancelCardService {

    private static final Logger logger = LoggerFactory.getLogger(CancelCardService.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public CancelCardService(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void cancelCard(@NotNull UUID cardId) {
        logger.info("[CardId={}] Request received to cancel the card", cardId);

        var key = Key.builder().partitionValue(cardId.toString()).build();
        var card = dynamoDbTable.getItem(key);

        if (Objects.isNull(card)) {
            logger.warn("[CardId={}] Card not found, cancellation request failed", cardId);
            throw new ResourceNotFoundException("card.notFound", cardId);
        }

        logger.info("[CardId={}] Card found. Cancelling the card", cardId);

        card.setCardStatus(CardStatus.CANCELLED);
        dynamoDbTable.putItem(card);

        logger.info("[CardId={}] Successfully cancelled the card", cardId);
    }
}

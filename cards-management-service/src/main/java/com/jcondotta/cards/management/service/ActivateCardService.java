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
public class ActivateCardService {

    private static final Logger logger = LoggerFactory.getLogger(ActivateCardService.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public ActivateCardService(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void activateCard(@NotNull UUID cardId) {
        logger.info("[CardId={}] Received request to activate card", cardId);

        var key = Key.builder().partitionValue(cardId.toString()).build();
        var card = dynamoDbTable.getItem(key);

        if (Objects.isNull(card)) {
            logger.warn("[CardId={}] Card not found", cardId);
            throw new ResourceNotFoundException("card.notFound", cardId);
        }

        logger.info("[CardId={}] Card found. Activating the card", cardId);

        card.setCardStatus(CardStatus.ACTIVE);
        dynamoDbTable.putItem(card);

        logger.info("[CardId={}] Successfully activated card", cardId);
    }
}

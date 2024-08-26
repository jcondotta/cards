package com.blitzar.cards.management.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import com.blitzar.cards.exception.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;

@Singleton
public class LockCardService {

    private static final Logger logger = LoggerFactory.getLogger(LockCardService.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public LockCardService(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void lockCard(String cardId){
        logger.info("[CardId={}] Locking card", cardId);

        var key = Key.builder().partitionValue(cardId).build();
        var card = dynamoDbTable.getItem(key);
        if(Objects.isNull(card)){
            throw new ResourceNotFoundException("card.notFound", cardId);
        }

        card.setCardStatus(CardStatus.LOCKED);
        dynamoDbTable.putItem(card);

        logger.info("[CardId={}] Card locked", cardId);
    }
}
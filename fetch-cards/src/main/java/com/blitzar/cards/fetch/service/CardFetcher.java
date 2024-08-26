package com.blitzar.cards.fetch.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.fetch.service.dto.CardDTO;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;

@Singleton
public class CardFetcher implements DataFetcher<CardDTO> {

    private static final Logger logger = LoggerFactory.getLogger(CardFetcher.class);

    @Inject
    private final DynamoDbTable<Card> dynamoDbTable;

    public CardFetcher(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    @Override
    public CardDTO get(DataFetchingEnvironment fetchingEnvironment) throws Exception {
        String cardId = fetchingEnvironment.getArgument("cardId");
        if(StringUtils.isBlank(cardId)){
            throw new GraphQLException("card.cardId.notBlank");
        }

        logger.info("[CardId={}] Attempting to fetch card", cardId);
        var key = Key.builder().partitionValue(cardId).build();

        var card = dynamoDbTable.getItem(key);
        if(Objects.isNull(card)){
            throw new GraphQLException("card.notFound");
        }

        logger.debug("[CardId={}] Card fetched: {}", card.getCardId(), card);
        return new CardDTO(card);
    }
}
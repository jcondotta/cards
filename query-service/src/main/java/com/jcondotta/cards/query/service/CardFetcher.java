package com.jcondotta.cards.query.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.query.service.dto.CardDTO;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.logstash.logback.argument.StructuredArguments;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class CardFetcher implements DataFetcher<CardDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardFetcher.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public CardFetcher(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    @Override
    public CardDTO get(DataFetchingEnvironment fetchingEnvironment) {
        String cardId = fetchingEnvironment.getArgument("cardId");

        try {
            if (StringUtils.isBlank(cardId)) {
                LOGGER.warn("Invalid request: cardId is blank");
                throw new GraphQLException("card.cardId.notBlank");
            }

            UUID.fromString(cardId); // Validate UUID format
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid UUID format",
                    StructuredArguments.keyValue("cardId", cardId),
                    e
            );
            throw new GraphQLException("Invalid card ID format", e);
        }

        LOGGER.info("Fetching card from database",
                StructuredArguments.keyValue("cardId", cardId)
        );

        var key = Key.builder().partitionValue(cardId).build();
        var card = dynamoDbTable.getItem(key);

        if (Objects.isNull(card)) {
            LOGGER.warn("Card not found",
                    StructuredArguments.keyValue("cardId", cardId)
            );
            throw new GraphQLException("card.notFound");
        }

        LOGGER.info("Card fetched successfully",
                StructuredArguments.keyValue("cardId", card.getCardId())
        );

        return new CardDTO(card);
    }
}

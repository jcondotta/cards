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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class CardsFetcher implements DataFetcher<List<CardDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardsFetcher.class);

    private final DynamoDbIndex<Card> dynamoDbIndex;

    @Inject
    public CardsFetcher(DynamoDbIndex<Card> dynamoDbIndex) {
        this.dynamoDbIndex = dynamoDbIndex;
    }

    @Override
    public List<CardDTO> get(DataFetchingEnvironment fetchingEnvironment) {
        String bankAccountId = fetchingEnvironment.getArgument("bankAccountId");

        if (StringUtils.isBlank(bankAccountId)) {
            LOGGER.warn("Invalid request: bankAccountId is blank");
            throw new GraphQLException("card.bankAccountId.notBlank");
        }

        try {
            UUID.fromString(bankAccountId);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid bank account ID format",
                    StructuredArguments.keyValue("bankAccountId", bankAccountId),
                    e
            );
            throw new GraphQLException("Invalid bank account ID format", e);
        }

        LOGGER.info("Fetching cards for bank account",
                StructuredArguments.keyValue("bankAccountId", bankAccountId)
        );

        var queryConditional = QueryConditional.keyEqualTo(builder -> builder.partitionValue(bankAccountId).build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        List<Page<Card>> paginatedCards = dynamoDbIndex.query(queryRequest)
                .stream()
                .toList();

        List<CardDTO> cards = paginatedCards.stream()
                .flatMap(page -> page.items().stream())
                .map(CardDTO::new)
                .collect(Collectors.toList());

        LOGGER.info("Cards fetched successfully",
                StructuredArguments.keyValue("bankAccountId", bankAccountId),
                StructuredArguments.keyValue("totalCards", cards.size())
        );

        return cards;
    }
}
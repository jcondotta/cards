package com.blitzar.cards.fetch.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.fetch.service.dto.CardDTO;
import com.blitzar.cards.fetch.service.dto.CardsDTO;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class CardsFetcher implements DataFetcher<List<CardDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(CardsFetcher.class);

    private final DynamoDbIndex<Card> dynamoDbIndex;

    @Inject
    public CardsFetcher(DynamoDbIndex<Card> dynamoDbIndex) {
        this.dynamoDbIndex = dynamoDbIndex;
    }

    @Override
    public List<CardDTO> get(DataFetchingEnvironment fetchingEnvironment) throws Exception {
        String bankAccountIdStr = fetchingEnvironment.getArgument("bankAccountId");

        if (StringUtils.isBlank(bankAccountIdStr)) {
            throw new GraphQLException("card.bankAccountId.notNull");
        }

        UUID bankAccountId;
        try {
            bankAccountId = UUID.fromString(bankAccountIdStr);
        } catch (IllegalArgumentException e) {
            throw new GraphQLException("Invalid bank account ID format", e);
        }

        var queryConditional = QueryConditional.keyEqualTo(builder -> builder.partitionValue(bankAccountId.toString()).build());
        logger.info("[BankAccountId={}] Attempting to fetch cards with limit: {}", bankAccountId);

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        final SdkIterable<Page<Card>> queryResult = dynamoDbIndex.query(queryRequest);
        List<CardDTO> cards = new ArrayList<>();

        for (Page<Card> page : queryResult) {
            cards.addAll(page.items().stream().map(CardDTO::new).collect(Collectors.toList()));
        }

        logger.info("[BankAccountId={}] Card(s) found: {}", bankAccountId, cards.size());
        return cards;
    }
}
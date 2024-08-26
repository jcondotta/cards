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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class CardsFetcher implements DataFetcher<List<CardDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(CardsFetcher.class);

    @Inject
    private final DynamoDbIndex<Card> dynamoDbIndex;

    public CardsFetcher(DynamoDbIndex<Card> dynamoDbIndex) {
        this.dynamoDbIndex = dynamoDbIndex;
    }

    @Override
    public List<CardDTO> get(DataFetchingEnvironment fetchingEnvironment) throws Exception {
        String bankAccountId = fetchingEnvironment.getArgument("bankAccountId");
        if(StringUtils.isBlank(bankAccountId)){
            throw new GraphQLException("card.bankAccountId.notBlank");
        }

        var queryConditional = QueryConditional.keyEqualTo(builder -> builder.partitionValue(bankAccountId).build());

        logger.info("[BankAccountId={}] Attempting to fetch cards", bankAccountId);
        final SdkIterable<Page<Card>> queryResult = dynamoDbIndex.query(queryConditional);

        List<CardDTO> cards = new ArrayList<>();
        queryResult.stream()
                .forEach(page -> page.items()
                        .forEach(card -> cards.add(new CardDTO(card))));

        logger.info("[BankAccountId={}] Card(s) found: {}", bankAccountId, cards.size());
        return cards;
    }
}
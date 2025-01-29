package com.jcondotta.cards.query.factory;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.aws.CardTableSchemaFactory;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Factory
public class DynamoDBCardsIndexFactory {

    @Value("${aws.dynamodb.tables.cards.global-secondary-indexes.cards-by-bank-account-id.name}")
    String cardsByBankAccountIdIndexName;

    @Singleton
    @Replaces(DynamoDbIndex.class)
    @Requires("${aws.dynamodb.tables.cards.global-secondary-indexes.cards-by-bank-account-id.name}")
    public DynamoDbIndex<Card> dynamoDbIndex(DynamoDbTable<Card> dynamoDbTable){
        return dynamoDbTable.index(cardsByBankAccountIdIndexName);
    }

    @Singleton
    @Replaces(TableSchema.class)
    public TableSchema<Card> cardTableSchema() {
        StaticTableSchema.Builder<Card> builder = CardTableSchemaFactory.baseSchemaBuilder();
        CardTableSchemaFactory.addBankAccountIdWithGSI(builder, cardsByBankAccountIdIndexName);

        return builder.build();
    }
}
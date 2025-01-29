package com.jcondotta.cards.core.factory.aws;

import com.jcondotta.cards.core.domain.Card;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Factory
public class DynamoDBCardsTableFactory {

    @Singleton
    @Requires(property = "aws.dynamodb.tables.cards.table-name")
    public DynamoDbTable<Card> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                             TableSchema<Card> cardTableSchema,
                                             @Value("${aws.dynamodb.tables.cards.table-name}") String cardsTableName){
        return dynamoDbEnhancedClient.table(cardsTableName, cardTableSchema);
    }

    @Singleton
    public TableSchema<Card> cardTableSchema() {
        StaticTableSchema.Builder<Card> builder = CardTableSchemaFactory.baseSchemaBuilder();

        CardTableSchemaFactory.addBankAccountIdWithoutGsi(builder);
        return builder.build();
    }
}
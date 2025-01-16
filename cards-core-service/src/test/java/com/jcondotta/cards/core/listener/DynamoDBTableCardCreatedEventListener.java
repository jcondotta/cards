package com.jcondotta.cards.core.listener;

import com.jcondotta.cards.core.domain.Card;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Singleton
public class DynamoDBTableCardCreatedEventListener implements BeanCreatedEventListener<DynamoDbTable<Card>> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTableCardCreatedEventListener.class);

    @Value("${aws.dynamodb.tables.cards.global-secondary-indexes.cards-by-bank-account-id.name}")
    String cardsByBankAccountIdIndexName;

    @Override
    public DynamoDbTable<Card> onCreated(@NonNull BeanCreatedEvent<DynamoDbTable<Card>> event){
        var dynamoDBTable = event.getBean();

        try {
            dynamoDBTable.describeTable();
            logger.debug("DynamoDB table for type {} exists.", dynamoDBTable.tableSchema().itemType());
        }
        catch (ResourceNotFoundException e) {
            logger.warn("DynamoDB table for type {} not found. Creating the table.", dynamoDBTable.tableSchema().itemType());
            dynamoDBTable.createTable(builder -> builder
                    .globalSecondaryIndices(gsiBuilder -> gsiBuilder
                            .indexName(cardsByBankAccountIdIndexName)
                            .projection(Projection.builder()
                                    .projectionType(ProjectionType.ALL)
                                    .build())
                    )
            );
        }
        catch (Exception e) {
            logger.error("An unexpected error occurred while checking the DynamoDB table: {}", e.getMessage(), e);
        }

        return dynamoDBTable;
    }
}
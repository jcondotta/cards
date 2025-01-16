package com.jcondotta.cards.query.listener;

import com.jcondotta.cards.core.domain.Card;
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

    @Override
    public DynamoDbTable<Card> onCreated(@NonNull BeanCreatedEvent<DynamoDbTable<Card>> event){
        var dynamoDBTable = event.getBean();

        try {
            dynamoDBTable.describeTable();
        }
        catch (ResourceNotFoundException e){
            logger.info("Creating DynamoDbTable from type: {}", dynamoDBTable.tableSchema().itemType());

            dynamoDBTable.createTable(builder -> builder
                    .globalSecondaryIndices(gsiBuilder -> gsiBuilder
                            .indexName("cards-by-bank-account-id-gsi")
                            .projection(Projection.builder()
                                    .projectionType(ProjectionType.ALL)
                                    .build()
                            )
                            .provisionedThroughput(provisionedThroughputBuilder -> provisionedThroughputBuilder
                                    .readCapacityUnits(3L)
                                    .writeCapacityUnits(3L)
                                    .build()
                            )
                    )
            );
        }

        return dynamoDBTable;
    }
}
package com.blitzar.cards.helper;

import com.blitzar.cards.domain.Card;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Singleton
public class CardTablePurgeService {

    private static final Logger logger = LoggerFactory.getLogger(CardTablePurgeService.class);

    private final DynamoDbTable<Card> dynamoDbTable;

    @Inject
    public CardTablePurgeService(DynamoDbTable<Card> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void purgeTable() {
        logger.info("Purging all items from {} DynamoDB table", dynamoDbTable.tableName());

        try {
            var cards = dynamoDbTable.scan()
                    .items().stream()
                    .toList();

            cards.forEach(dynamoDbTable::deleteItem);
            logger.info("Successfully purged {} items.", cards.size());
        }
        catch (Exception e) {
            logger.error("Error purging cards items from DynamoDB table", e);
        }
    }
}
package com.blitzar.cards.container;

import org.testcontainers.containers.localstack.LocalStackContainer;

import java.util.HashMap;
import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service;

public class DynamoDBConstants {

    public static class CardsTable {
        public static final String DYNAMODB_CARDS_TABLE_NAME = "cards-test";

        public static Map<String, String> getTableProperties() {
            Map<String, String> tableProperties = new HashMap<>();
            tableProperties.put("AWS_DYNAMODB_CARDS_TABLE_NAME", DYNAMODB_CARDS_TABLE_NAME);
            return tableProperties;
        }
    }

    public static class CardsByBankAccountIdGSI {
        public static final String DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME = "cards-by-bank-account-id-gsi-test";
        public static final Long DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS = 3L;
        public static final Long DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS = 3L;

        public static Map<String, String> getGSIProperties() {
            Map<String, String> gsiProperties = new HashMap<>();
            gsiProperties.put("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME);
            gsiProperties.put("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS.toString());
            gsiProperties.put("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS.toString());
            return gsiProperties;
        }
    }

    public static Map<String, String> getDynamoDBProperties(LocalStackContainer localStackContainer) {
        Map<String, String> dynamoDBProperties = new HashMap<>();

        dynamoDBProperties.put("AWS_DYNAMODB_ENDPOINT", localStackContainer.getEndpointOverride(Service.DYNAMODB).toString());

        dynamoDBProperties.putAll(CardsTable.getTableProperties());
        dynamoDBProperties.putAll(CardsByBankAccountIdGSI.getGSIProperties());

        return dynamoDBProperties;
    }

    public static String prettyPrint(LocalStackContainer localStackContainer) {
        return new StringBuilder()
            .append("\nDynamoDB Cards Table:\n")
            .append(String.format("  DynamoDB endpoint: %s%n", localStackContainer.getEndpointOverride(Service.DYNAMODB)))
            .append(String.format("  Cards table name: %s%n", CardsTable.DYNAMODB_CARDS_TABLE_NAME))
            .append("\n")
            .append("DynamoDB Cards GSI:\n")
            .append(String.format("  GSI name: %s%n", CardsByBankAccountIdGSI.DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME))
            .append(String.format("  GSI read capacity: %s units%n", CardsByBankAccountIdGSI.DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS))
            .append(String.format("  GSI write capacity: %s units%n", CardsByBankAccountIdGSI.DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS))
            .toString();
    }
}

package com.blitzar.cards.container;

import org.testcontainers.containers.localstack.LocalStackContainer;

import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.*;

public class DynamoDBConstants {

    public static final String DYNAMODB_CARDS_TABLE_NAME = "cards-test";
    public static final String DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME = "cards-by-bank-account-id-gsi-test";
    public static final Long DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS = 3L;
    public static final Long DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS = 3L;

    public static Map.Entry<String, String>[] getDynamoDBPropertyEntries(LocalStackContainer localStackContainer) {
        return new Map.Entry[]{
                Map.entry("AWS_DYNAMODB_ENDPOINT", localStackContainer.getEndpointOverride(Service.DYNAMODB).toString()),
                Map.entry("AWS_DYNAMODB_CARDS_TABLE_NAME", DYNAMODB_CARDS_TABLE_NAME),
                Map.entry("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME),
                Map.entry("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS.toString()),
                Map.entry("AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS.toString())
        };
    }

    public static String prettyPrint(LocalStackContainer localStackContainer) {
        return new StringBuilder()
            .append("DynamoDB:\n")
            .append(String.format("  DynamoDB endpoint: %s%n", localStackContainer.getEndpointOverride(Service.DYNAMODB)))
            .append(String.format("  DynamoDB cards table name: %s%n", DYNAMODB_CARDS_TABLE_NAME))
            .append(String.format("  DynamoDB GSI cards by bank account id name: %s%n", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME))
            .append(String.format("  DynamoDB GSI cards by bank account id read capacity units: %s%n", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_READ_CAPACITY_UNITS))
            .append(String.format("  DynamoDB GSI cards by bank account id write capacity units: %s%n", DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_WRITE_CAPACITY_UNITS))
            .toString();
    }
}
package com.blitzar.cards.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.dynamodb")
public record DynamoDbConfiguration(
        String endpoint,
        TablesConfiguration tables,
        GlobalSecondaryIndexesConfiguration globalSecondaryIndexes
) {

    @ConfigurationProperties("tables")
    public record TablesConfiguration(
            CardsTableConfiguration cards
    ) {}

    @ConfigurationProperties("tables.cards")
    public record CardsTableConfiguration(
            String tableName
    ) {}

    @ConfigurationProperties("global-secondary-indexes")
    public record GlobalSecondaryIndexesConfiguration(
            CardsByBankAccountIdGsiConfiguration cardsByBankAccountId
    ) {}

    @ConfigurationProperties("global-secondary-indexes.cards-by-bank-account-id")
    public record CardsByBankAccountIdGsiConfiguration(
            String name,
            Long readCapacityUnits,
            Long writeCapacityUnits
    ) {}
}

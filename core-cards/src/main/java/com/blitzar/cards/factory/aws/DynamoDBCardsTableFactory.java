package com.blitzar.cards.factory.aws;

import com.blitzar.cards.configuration.DynamoDbConfiguration;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

import java.time.LocalDateTime;
import java.util.UUID;

@Factory
public class DynamoDBCardsTableFactory {

    @Inject
    private DynamoDbConfiguration dynamoDbConfiguration;

    @Singleton
    @Requires(property = "aws.dynamodb.tables.cards.table-name")
    public DynamoDbTable<Card> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, TableSchema<Card> cardTableSchema){
        var cardsTableConfiguration = dynamoDbConfiguration.tables().cards();
        return dynamoDbEnhancedClient.table(cardsTableConfiguration.tableName(), cardTableSchema);
    }

    @Singleton
    public DynamoDbIndex<Card> dynamoDbIndex(DynamoDbTable<Card> dynamoDbTable){
        var cardsByBankAccountIdGsiConfiguration = dynamoDbConfiguration
                .globalSecondaryIndexes().cardsByBankAccountId();

        return dynamoDbTable.index(cardsByBankAccountIdGsiConfiguration.name());
    }

    @Singleton
    public TableSchema<Card> cardTableSchema() {
        return StaticTableSchema.builder(Card.class)
                .newItemSupplier(Card::new)
                .addAttribute(UUID.class, attr -> attr.name("cardId")
                        .getter(Card::getCardId)
                        .setter(Card::setCardId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(UUID.class, attr -> attr.name("bankAccountId")
                        .getter(Card::getBankAccountId)
                        .setter(Card::setBankAccountId)
                        .tags(StaticAttributeTags
                                .secondaryPartitionKey(dynamoDbConfiguration.globalSecondaryIndexes().cardsByBankAccountId().name())))
                .addAttribute(String.class, attr -> attr.name("cardholderName")
                        .getter(Card::getCardholderName)
                        .setter(Card::setCardholderName))
                .addAttribute(String.class, attr -> attr.name("cardNumber")
                        .getter(Card::getCardNumber)
                        .setter(Card::setCardNumber))
                .addAttribute(CardStatus.class, attr -> attr.name("cardStatus")
                        .getter(Card::getCardStatus)
                        .setter(Card::setCardStatus))
                .addAttribute(Integer.class, attr -> attr.name("dailyWithdrawalLimit")
                        .getter(Card::getDailyWithdrawalLimit)
                        .setter(Card::setDailyWithdrawalLimit))
                .addAttribute(Integer.class, attr -> attr.name("dailyPaymentLimit")
                        .getter(Card::getDailyPaymentLimit)
                        .setter(Card::setDailyPaymentLimit))
                .addAttribute(LocalDateTime.class, attr -> attr.name("expirationDate")
                        .getter(Card::getExpirationDate)
                        .setter(Card::setExpirationDate))
                .addAttribute(LocalDateTime.class, attr -> attr.name("createdAt")
                        .getter(Card::getCreatedAt)
                        .setter(Card::setCreatedAt))
                .build();
    }
}
package com.blitzar.cards.factory.aws;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
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
public class DynamoDBTableFactory {

    @Singleton
    @Requires(property = "aws.dynamodb.table-name")
    public DynamoDbTable<Card> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, TableSchema<Card> cardTableSchema,
                                             @Value("${aws.dynamodb.table-name}") String tableName){
        return dynamoDbEnhancedClient.table(tableName, cardTableSchema);
    }

    @Singleton
    public DynamoDbIndex<Card> dynamoDbIndex(DynamoDbTable<Card> dynamoDbTable){
        return dynamoDbTable.index("cards-by-bank-account-id-gsi");
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
                        .tags(StaticAttributeTags.secondaryPartitionKey("cards-by-bank-account-id-gsi")))
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
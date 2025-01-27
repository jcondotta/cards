package com.jcondotta.cards.core.factory.aws;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

import java.time.LocalDateTime;
import java.util.UUID;

public class CardTableSchemaFactory {

    private CardTableSchemaFactory() {}

    public static StaticTableSchema.Builder<Card> baseSchemaBuilder() {
        return StaticTableSchema.builder(Card.class)
                .newItemSupplier(Card::new)
                .addAttribute(UUID.class, attr -> attr.name("cardId")
                        .getter(Card::getCardId)
                        .setter(Card::setCardId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
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
                        .setter(Card::setCreatedAt));
    }

    public static void addBankAccountIdWithoutGsi(StaticTableSchema.Builder<Card> builder) {
        builder.addAttribute(UUID.class, attr -> attr.name("bankAccountId")
                .getter(Card::getBankAccountId)
                .setter(Card::setBankAccountId));
    }

    public static void addBankAccountIdWithGSI(StaticTableSchema.Builder<Card> builder, String gsiName) {
        builder.addAttribute(UUID.class, attr -> attr.name("bankAccountId")
                .getter(Card::getBankAccountId)
                .setter(Card::setBankAccountId)
                .tags(StaticAttributeTags.secondaryPartitionKey(gsiName)));
    }
}

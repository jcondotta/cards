package com.jcondotta.cards.core.domain;

import io.micronaut.serde.annotation.Serdeable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDateTime;
import java.util.UUID;

@Serdeable
@DynamoDbBean
public class Card {

    private UUID cardId;

    private UUID bankAccountId;

    private String cardholderName;

    private String cardNumber;

    private CardStatus cardStatus;

    private LocalDateTime expirationDate;

    private int dailyWithdrawalLimit;

    private int dailyPaymentLimit;

    private LocalDateTime createdAt;

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(UUID bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(int dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }

    public int getDailyPaymentLimit() {
        return dailyPaymentLimit;
    }

    public void setDailyPaymentLimit(int dailyPaymentLimit) {
        this.dailyPaymentLimit = dailyPaymentLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "{" +
                "cardId='" + cardId + '\'' +
                ", bankAccountId=" + bankAccountId +
                ", cardholderName='" + cardholderName + '\'' +
                ", cardStatus=" + cardStatus +
                ", expirationDate=" + expirationDate +
                ", dailyWithdrawalLimit=" + dailyWithdrawalLimit +
                ", dailyPaymentLimit=" + dailyPaymentLimit +
                '}';
    }
}

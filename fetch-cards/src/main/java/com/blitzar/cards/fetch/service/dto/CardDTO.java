package com.blitzar.cards.fetch.service.dto;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Serdeable
public record CardDTO (
        String bankAccountId,
        String cardId,
        String cardholderName,
        String cardNumber,
        CardStatus cardStatus,
        int dailyWithdrawalLimit,
        int dailyPaymentLimit,
        LocalDateTime createdAt,
        LocalDateTime expirationDate) {

    public CardDTO(Card card) {
        this(card.getBankAccountId(),
                card.getCardId(),
                card.getCardholderName(),
                card.getCardNumber(),
                card.getCardStatus(),
                card.getDailyWithdrawalLimit(),
                card.getDailyPaymentLimit(),
                card.getCreatedAt(),
                card.getExpirationDate());
    }
}

package com.blitzar.cards.web.events.request;

import com.blitzar.cards.domain.CardStatus;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record AddCardRequest(
        @NotBlank(message = "card.bankAccountId.notBlank") String bankAccountId,
        @NotBlank(message = "card.cardholderName.notBlank") @Size(max = 21, message = "card.cardholderName.length.limit") String cardholderName) {

    public static final CardStatus DEFAULT_CARD_STATUS = CardStatus.LOCKED;
    public static final int DEFAULT_DAILY_WITHDRAWAL_LIMIT = 1000;
    public static final int DEFAULT_DAILY_PAYMENT_LIMIT = 2500;
    public static final int DEFAULT_YEAR_PERIOD_EXPIRATION_DATE = 5;

}
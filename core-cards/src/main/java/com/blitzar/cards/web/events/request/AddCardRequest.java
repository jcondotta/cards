package com.blitzar.cards.web.events.request;

import com.blitzar.cards.domain.CardStatus;
import com.blitzar.cards.validation.annotation.CardholderName;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
@Schema(description = "Request object for adding a new card.")
public record AddCardRequest(

        @Schema(description = "The bank account ID associated with the card.",
                example = "e8c8be4e-38b3-4c28-88c2-d22f4ef2e34f",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "card.bankAccountId.notNull")
        UUID bankAccountId,

        @Schema(description = "The cardholder's name that will appear on the card.",
                example = "Jefferson Condotta",
                requiredMode = RequiredMode.REQUIRED)
        @CardholderName
        String cardholderName
) {
    public static final CardStatus DEFAULT_CARD_STATUS = CardStatus.LOCKED;
    public static final int DEFAULT_DAILY_WITHDRAWAL_LIMIT = 1000;
    public static final int DEFAULT_DAILY_PAYMENT_LIMIT = 2500;
    public static final int DEFAULT_YEAR_PERIOD_EXPIRATION_DATE = 5;
}

package com.jcondotta.cards.core.request;

import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.validation.annotation.SecureInput;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        @NotBlank(message = "card.cardholderName.notBlank")
        @Size(max = 31, message = "card.cardholderName.tooLong")
        @SecureInput(message = "card.cardholderName.invalid")
        String cardholderName
) {
    public static final CardStatus DEFAULT_CARD_STATUS = CardStatus.LOCKED;
    public static final int DEFAULT_DAILY_WITHDRAWAL_LIMIT = 1000;
    public static final int DEFAULT_DAILY_PAYMENT_LIMIT = 2500;
    public static final int DEFAULT_YEAR_PERIOD_EXPIRATION_DATE = 5;
}

package com.blitzar.cards.fetch.service.dto;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "A DTO representing the details of a card.")
public record CardDTO(

        @Schema(description = "The unique identifier for the card.",
                example = "0d0c3534-1887-4a8d-9fe0-f25870813207",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull UUID cardId,

        @Schema(description = "The unique identifier for the bank account associated with the card.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull UUID bankAccountId,

        @Schema(description = "The name of the cardholder.",
                example = "Jefferson Condotta",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull String cardholderName,

        @Schema(description = "The card number.",
                example = "1234-5678-9876-5432",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull String cardNumber,

        @Schema(description = "The status of the card.",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull CardStatus cardStatus,

        @Schema(description = "The daily withdrawal limit for the card.",
                example = "500",
                requiredMode = RequiredMode.REQUIRED)
        int dailyWithdrawalLimit,

        @Schema(description = "The daily payment limit for the card.",
                example = "1000",
                requiredMode = RequiredMode.REQUIRED)
        int dailyPaymentLimit,

        @Schema(description = "The creation timestamp of the card.",
                example = "2024-10-01T12:00:00",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull LocalDateTime createdAt,

        @Schema(description = "The expiration date of the card.",
                example = "2025-10-01T12:00:00",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull LocalDateTime expirationDate
) {

    public CardDTO(Card card) {
        this(
                card.getCardId(),
                card.getBankAccountId(),
                card.getCardholderName(),
                card.getCardNumber(),
                card.getCardStatus(),
                card.getDailyWithdrawalLimit(),
                card.getDailyPaymentLimit(),
                card.getCreatedAt(),
                card.getExpirationDate());
    }
}

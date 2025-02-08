package com.jcondotta.cards.core.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "A DTO representing a list of cards with pagination details.")
public record CardsDTO(

        @Schema(description = "A list of card details.",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull Collection<CardDTO> cards,

        @Schema(description = "The total number of cards returned.",
                example = "25",
                requiredMode = RequiredMode.REQUIRED)
        int count,

        @Schema(description = "The last evaluated key used for pagination, which allows fetching the next page of results.",
                requiredMode = RequiredMode.NOT_REQUIRED)
        LastEvaluatedKey lastEvaluatedKey
) {

    public CardsDTO(Collection<CardDTO> cards) {
        this(cards, cards.size(), null);
    }

    public CardsDTO(CardDTO cardDTO) {
        this(List.of(cardDTO), 1, null);
    }

    @Override
    public Collection<CardDTO> cards() {
        return Objects.nonNull(cards) ? cards : new ArrayList<>();
    }
}
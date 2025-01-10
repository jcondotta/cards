package com.blitzar.cards.fetch.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "Last evaluated key for pagination in DynamoDB queries.")
public record LastEvaluatedKey(

        @Schema(description = "Unique identifier for the card used in pagination.",
                example = "0d0c3534-1887-4a8d-9fe0-f25870813207",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull UUID cardId

) {

    public Map<String, AttributeValue> toExclusiveStartKey() {
        Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();

        exclusiveStartKey.put("cardId", AttributeValue.builder()
                .s(cardId.toString())
                .build());

        return exclusiveStartKey;
    }
}

package com.blitzar.cards.manage.web.controller;

import com.blitzar.cards.manage.service.LockCardService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@Validated
@Controller(ManageCardAPIConstants.CARD_ID_API_V1_MAPPING)
public class LockCardController {

    private final LockCardService lockCardService;

    @Inject
    public LockCardController(LockCardService lockCardService) {
        this.lockCardService = lockCardService;
    }

    @Patch(value = "/lock")
    @Operation(summary = "Lock a card",
            description = "Locks a card by its card ID. If the card exists, its status will be updated to 'LOCKED'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully locked."),
            @ApiResponse(responseCode = "404", description = "Card not found. Ensure that the card ID is correct."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues or database failures.")
    })
    public HttpResponse<?> lockCard(
            @PathVariable("card-id")
            @Schema(description = "The unique identifier of the card to be locked", requiredMode = RequiredMode.REQUIRED) UUID cardId) {
        lockCardService.lockCard(cardId);
        return HttpResponse.noContent();
    }
}

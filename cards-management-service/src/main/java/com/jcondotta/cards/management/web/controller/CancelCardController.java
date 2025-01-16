package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.management.service.CancelCardService;
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
public class CancelCardController {

    private final CancelCardService cancelCardService;

    @Inject
    public CancelCardController(CancelCardService cancelCardService) {
        this.cancelCardService = cancelCardService;
    }

    @Patch(value = "/cancellation")
    @Operation(summary = "Cancel a card",
            description = "Cancels a card by its card ID. If the card is found, it will be marked as 'CANCELLED'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully cancelled."),
            @ApiResponse(responseCode = "404", description = "Card not found. Ensure that the card ID is correct."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues or database failures.")
    })
    public HttpResponse<?> cancelCard(
            @PathVariable("card-id")
            @Schema(description = "The unique identifier of the card to be cancelled", requiredMode = RequiredMode.REQUIRED) UUID cardId) {
        cancelCardService.cancelCard(cardId);
        return HttpResponse.noContent();
    }
}

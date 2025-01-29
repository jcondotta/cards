package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.management.service.ActivateCardService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;

import java.util.UUID;

@Validated
@Controller(CardApiPaths.BY_ID)
public class ActivateCardController {

    private final ActivateCardService activateCardService;

    @Inject
    public ActivateCardController(ActivateCardService activateCardService) {
        this.activateCardService = activateCardService;
    }

    @Patch(value = "/activation")
    @Operation(summary = "Activate a card",
            description = "Activates a card by its card ID. If the card is found and is eligible, its status will be updated to 'ACTIVE'."
                    + " The card must exist in the system, and the request will return an empty response if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully activated."),
            @ApiResponse(responseCode = "404", description = "Card not found. Ensure that the card ID is correct."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues or database failures.")
    })
    public HttpResponse<?> activateCard(
            @PathVariable("card-id")
            @Schema(description = "The unique identifier of the card to be activated", requiredMode = RequiredMode.REQUIRED) UUID cardId) {
        activateCardService.activateCard(cardId);
        return HttpResponse.noContent();
    }
}

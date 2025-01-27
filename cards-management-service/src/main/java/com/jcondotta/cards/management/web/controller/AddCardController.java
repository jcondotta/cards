package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.management.service.AddCardService;
import io.micronaut.context.LocalizedMessageSource;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;

import java.util.Locale;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Validated
@Controller(CardApiPaths.BASE_V1_PATH)
public class AddCardController {

    private final AddCardService addCardService;
    private final LocalizedMessageSource messageSource;

    @Inject
    public AddCardController(AddCardService addCardService, LocalizedMessageSource messageSource) {
        this.addCardService = addCardService;
        this.messageSource = messageSource;
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new card",
            description = "This endpoint allows users to add a new card. The request body should contain necessary details such as the bank account ID and cardholder's name."
                    + " If successful, it returns an accepted response with a localized message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Card successfully added.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. Ensure all required fields are correctly populated."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues or unexpected errors during processing.")
    })
    public HttpResponse<?> addCard(
            @Body @Schema(description = "Request body containing bankAccountId and cardholderName", requiredMode = RequiredMode.REQUIRED) AddCardRequest addCardRequest,
            HttpRequest<?> request) {

        var locale = request.getLocale().orElse(Locale.getDefault());
        var message = messageSource.getMessage("card.add.request.received", locale)
                .orElseThrow();

        addCardService.addCard(addCardRequest);

        return HttpResponse.accepted().body(message);
    }
}

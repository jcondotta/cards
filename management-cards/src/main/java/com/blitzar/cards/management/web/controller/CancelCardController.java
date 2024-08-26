package com.blitzar.cards.management.web.controller;

import com.blitzar.cards.management.service.CancelCardService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

@Validated
@Controller(ManagementCardAPIConstants.MANAGEMENT_CARD_V1_MAPPING)
public class CancelCardController {

    private final CancelCardService cancelCardService;

    @Inject
    public CancelCardController(CancelCardService cancelCardService) {
        this.cancelCardService = cancelCardService;
    }

    @Patch(value = "/cancellation")
    public HttpResponse<?> cancelCard(@PathVariable("card-id") String cardId){
        cancelCardService.cancelCard(cardId);
        return HttpResponse.noContent();
    }
}
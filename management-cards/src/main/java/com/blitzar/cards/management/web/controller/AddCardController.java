package com.blitzar.cards.management.web.controller;

import com.blitzar.cards.management.service.AddCardService;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.LocalizedMessageSource;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

import java.util.Locale;

@Validated
@Controller(CardAPIConstants.CARD_BASE_PATH_API_V1_MAPPING)
public class AddCardController {

    private final AddCardService addCardService;
    private final LocalizedMessageSource messageSource;

    @Inject
    public AddCardController(AddCardService addCardService, LocalizedMessageSource messageSource) {
        this.addCardService = addCardService;
        this.messageSource = messageSource;
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> addCard(@Body AddCardRequest addCardRequest, HttpRequest<?> request){
        var locale = request.getLocale().orElse(Locale.getDefault());
        var message = messageSource.getMessage("card.request", locale)
                .orElseThrow();

        addCardService.addCard(addCardRequest);

        return HttpResponse.accepted().body(message);
    }
}
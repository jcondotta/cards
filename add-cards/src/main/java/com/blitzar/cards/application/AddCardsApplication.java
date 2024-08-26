package com.blitzar.cards.application;

import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.runtime.Micronaut;
import io.micronaut.serde.annotation.SerdeImport;

@SerdeImport(AddCardRequest.class) //TODO dar uma olhada nisso
public class AddCardsApplication {

    public static void main(String[] args) {
        Micronaut.run(AddCardsApplication.class, args);
    }
}
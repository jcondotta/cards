package com.blitzar.cards.fetch;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

public class FetchCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(FetchCardsApplication.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}
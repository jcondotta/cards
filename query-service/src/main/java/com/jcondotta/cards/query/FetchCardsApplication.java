package com.jcondotta.cards.query;

import io.micronaut.runtime.Micronaut;

public class FetchCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(FetchCardsApplication.class)
                .start();
    }
}
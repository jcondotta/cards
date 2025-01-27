package com.jcondotta.cards.process;

import io.micronaut.runtime.Micronaut;

public class AddCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(AddCardsApplication.class)
                .start();
    }
}
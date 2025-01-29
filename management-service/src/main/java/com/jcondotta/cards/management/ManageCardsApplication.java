package com.jcondotta.cards.management;

import io.micronaut.runtime.Micronaut;

public class ManageCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(ManageCardsApplication.class)
                .start();
    }
}
package com.blitzar.cards.manage;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

public class ManageCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(ManageCardsApplication.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}
package com.blitzar.cards.management;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

public class ManagementCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(ManagementCardsApplication.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}
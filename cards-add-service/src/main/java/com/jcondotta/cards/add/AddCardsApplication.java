package com.jcondotta.cards.add;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

//@SerdeImport(AddCardRequest.class) //TODO dar uma olhada nisso
public class AddCardsApplication {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(AddCardsApplication.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}
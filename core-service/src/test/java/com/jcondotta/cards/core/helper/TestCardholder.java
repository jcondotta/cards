package com.jcondotta.cards.core.helper;

public enum TestCardholder {

    JEFFERSON("Jefferson Condotta"),
    PATRIZIO("Patrizio Condotta");

    private final String cardholderName;

    TestCardholder(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardholderName() {
        return cardholderName;
    }
}
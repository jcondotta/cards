package com.blitzar.cards.application.service;

public enum TestCardholder {

    JEFFERSON("Jefferson Condotta");

    private final String cardholderName;

    TestCardholder(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardholderName() {
        return cardholderName;
    }
}
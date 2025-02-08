package com.jcondotta.cards.core.helper;

import java.util.UUID;

public enum TestCard {

    JEFFERSON(UUID.fromString("d17fa947-3044-44e0-8f95-641a170908e6"));

    private final UUID cardId;

    TestCard(UUID cardId) {
        this.cardId = cardId;
    }

    public UUID getCardId() {
        return cardId;
    }
}
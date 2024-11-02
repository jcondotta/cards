package com.blitzar.cards.helper;

import java.util.UUID;

public enum TestBankAccount {

    BRAZIL(UUID.fromString("01920bff-1338-7efd-ade6-e9128debe5d4"));

    private final UUID bankAccountId;

    TestBankAccount(UUID bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }
}
package com.jcondotta.cards.core.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("tables.cards.global-secondary-indexes.cards-by-bank-account-id")
public class CardsByBankAccountIdGsiConfiguration {
    private String name;
    private Long readCapacityUnits;
    private Long writeCapacityUnits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReadCapacityUnits() {
        return readCapacityUnits;
    }

    public void setReadCapacityUnits(Long readCapacityUnits) {
        this.readCapacityUnits = readCapacityUnits;
    }

    public Long getWriteCapacityUnits() {
        return writeCapacityUnits;
    }

    public void setWriteCapacityUnits(Long writeCapacityUnits) {
        this.writeCapacityUnits = writeCapacityUnits;
    }
}
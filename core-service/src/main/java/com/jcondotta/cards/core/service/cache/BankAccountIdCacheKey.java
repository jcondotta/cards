package com.jcondotta.cards.core.service.cache;

import java.util.Objects;
import java.util.UUID;

public record BankAccountIdCacheKey(UUID bankAccountId) implements CacheKey {

    public static final String BANK_ACCOUNT_ID_CACHE_KEY_TEMPLATE = "cards:bank-account-id:%s";

    public BankAccountIdCacheKey(UUID bankAccountId) {
        this.bankAccountId = Objects.requireNonNull(bankAccountId, "cache.cards.bankAccountId.notNull");
    }

    @Override
    public String getKey() {
        return String.format(BANK_ACCOUNT_ID_CACHE_KEY_TEMPLATE, bankAccountId);
    }
}

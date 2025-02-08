package com.jcondotta.cards.core.service.cache;

import com.jcondotta.cards.core.helper.TestBankAccount;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountIdCacheKeyTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @Test
    void shouldReturnCacheKey_whenValidBankAccountIdIsProvided() {
        var cardsBankAccountIdCacheKey = new BankAccountIdCacheKey(BANK_ACCOUNT_ID_BRAZIL);

        var expectedCacheKey = buildExpectedCacheKey(BANK_ACCOUNT_ID_BRAZIL);

        assertThat(cardsBankAccountIdCacheKey.getKey()).isEqualTo(expectedCacheKey);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> new BankAccountIdCacheKey(null));

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.cards.bankAccountId.notNull"));
    }

    private String buildExpectedCacheKey(UUID bankAccountId){
        return new StringJoiner(":")
                .add("cards")
                .add("bank-account-id:" + bankAccountId)
                .toString();
    }
}
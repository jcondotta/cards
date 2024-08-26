package com.blitzar.cards.management.factory;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.web.events.request.AddCardRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CardTestFactory {

    private final static Clock testClockFixedInstant = new ClockTestFactory().testClockFixedInstant();

    public static Card buildCard(String cardId, String bankAccountId, String cardholderName){
        var card = new Card();
        card.setCardId(cardId);
        card.setBankAccountId(bankAccountId);
        card.setCardholderName(cardholderName);
        card.setCardNumber(UUID.randomUUID().toString());
        card.setCardStatus(AddCardRequest.DEFAULT_CARD_STATUS);
        card.setDailyWithdrawalLimit(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT);
        card.setDailyPaymentLimit(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT);
        card.setCreatedAt(LocalDateTime.now(testClockFixedInstant));
        card.setExpirationDate(LocalDateTime.now(testClockFixedInstant)
                .plus(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE, ChronoUnit.YEARS));

        return card;
    }

    public static Card buildCard(String bankAccountId, String cardholderName){
        return buildCard(UUID.randomUUID().toString(), bankAccountId, cardholderName);
    }

    public static Card buildCard(AddCardRequest addCardRequest){
        return buildCard(UUID.randomUUID().toString(), addCardRequest.bankAccountId(), addCardRequest.cardholderName());
    }
}

package com.blitzar.cards.factory;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.web.events.request.AddCardRequest;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CardTestFactory {

    public static Card buildCard(UUID cardId, UUID bankAccountId, String cardholderName){
        var card = new Card();
        card.setCardId(cardId);
        card.setBankAccountId(bankAccountId);
        card.setCardholderName(cardholderName);

        var cardNumber = new Faker().finance().creditCard();
        card.setCardNumber(cardNumber);
        card.setCardStatus(AddCardRequest.DEFAULT_CARD_STATUS);
        card.setDailyWithdrawalLimit(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT);
        card.setDailyPaymentLimit(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT);
        card.setCreatedAt(LocalDateTime.now(ClockTestFactory.testClockFixedInstant));
        card.setExpirationDate(LocalDateTime.now(ClockTestFactory.testClockFixedInstant)
                .plus(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE, ChronoUnit.YEARS));

        return card;
    }

    public static Card buildCard(UUID bankAccountId, String cardholderName){
        return buildCard(UUID.randomUUID(), bankAccountId, cardholderName);
    }

    public static Card buildCard(AddCardRequest addCardRequest){
        return buildCard(UUID.randomUUID(), addCardRequest.bankAccountId(), addCardRequest.cardholderName());
    }
}

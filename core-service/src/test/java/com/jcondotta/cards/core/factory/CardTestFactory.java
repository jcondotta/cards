package com.jcondotta.cards.core.factory;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.request.AddCardRequest;
import net.datafaker.Faker;

import java.time.LocalDateTime;
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
                .plusYears(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE));

        return card;
    }

    public static Card buildCard(UUID bankAccountId, String cardholderName){
        return buildCard(UUID.randomUUID(), bankAccountId, cardholderName);
    }
}

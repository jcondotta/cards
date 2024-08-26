package com.blitzar.cards.management.service;

import com.blitzar.cards.management.web.CardApplicationEventProducer;
import com.blitzar.cards.web.events.request.AddCardRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AddCardService {

    private static final Logger logger = LoggerFactory.getLogger(AddCardService.class);
    private final CardApplicationEventProducer eventProducer;

    @Inject
    public AddCardService(CardApplicationEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void addCard(AddCardRequest request){
        logger.info("[BankAccountId={}, CardholderName={}] Putting a message on SQS Queue for adding a new card", request.bankAccountId(), request.cardholderName());

        eventProducer.send(request);

        logger.info("[BankAccountId={}, CardholderName={}] Message sent", request.bankAccountId(), request.cardholderName());
    }
}
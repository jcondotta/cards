package com.blitzar.cards.application.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.blitzar.cards.application.service.AddCardService;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CardApplicationEventHandler extends MicronautRequestHandler<SQSEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(CardApplicationEventHandler.class);

    @Inject
    private AddCardService addCardService;

    @Inject
    private JsonMapper jsonMapper;

    public CardApplicationEventHandler() {}

    public CardApplicationEventHandler(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public Void execute(SQSEvent sqsEvent) {
        for(SQSEvent.SQSMessage message : sqsEvent.getRecords()){
            try {
                var addCardRequest = jsonMapper.readValue(message.getBody(), AddCardRequest.class);
                logger.info("[BankAccountId={}, CardholderName={}] Received a card application event", addCardRequest.bankAccountId(), addCardRequest.cardholderName());

                addCardService.addCard(addCardRequest);
            }
            catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return null;
    }

//    @Override
//    protected ApplicationContextBuilder newApplicationContextBuilder() {
//        ApplicationContextBuilder builder = super.newApplicationContextBuilder();
//        builder.eagerInitSingletons(true);
//        return builder;
//    }
}
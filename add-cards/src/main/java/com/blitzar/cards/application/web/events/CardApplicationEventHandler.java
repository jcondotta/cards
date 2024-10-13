package com.blitzar.cards.application.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.blitzar.cards.application.service.AddCardService;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
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

    public CardApplicationEventHandler() {
        super();
    }

    public CardApplicationEventHandler(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public Void execute(SQSEvent sqsEvent) {
        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Processing SQS message: {}", message.getBody());
                }

                AddCardRequest addCardRequest = jsonMapper.readValue(message.getBody(), AddCardRequest.class);

                logger.info("[BankAccountId={}, CardholderName={}] Received a card application event",
                        addCardRequest.bankAccountId(), addCardRequest.cardholderName());

                addCardService.addCard(addCardRequest);

            }
            catch (IOException e) {
                logger.error("Failed to process SQS message (MessageId={}, Body={}): {}",
                        message.getMessageId(), message.getBody(), e.getMessage(), e);

                throw new RuntimeException("Failed to process SQS message with MessageId: " + message.getMessageId(), e);
            }
        }

        return null;
    }
}

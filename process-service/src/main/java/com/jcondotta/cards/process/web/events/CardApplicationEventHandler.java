package com.jcondotta.cards.process.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.jcondotta.cards.core.request.AddCardRequest;
import com.jcondotta.cards.process.service.AddCardService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Inject;
import net.logstash.logback.argument.StructuredArguments;
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
                    logger.debug("Processing SQS message",
                            StructuredArguments.keyValue("messageId", message.getMessageId()),
                            StructuredArguments.keyValue("body", message.getBody())
                    );
                }

                AddCardRequest addCardRequest = jsonMapper.readValue(message.getBody(), AddCardRequest.class);

                logger.info("Received a card application event",
                        StructuredArguments.keyValue("bankAccountId", addCardRequest.bankAccountId()),
                        StructuredArguments.keyValue("cardholderName", addCardRequest.cardholderName())
                );

                addCardService.addCard(addCardRequest);

            } catch (IOException e) {
                logger.error("Failed to process SQS message. reason: {}",
                        e.getMessage(),
                        StructuredArguments.keyValue("messageId", message.getMessageId()),
                        StructuredArguments.keyValue("body", message.getBody()),
                        e
                );

                throw new RuntimeException("Failed to process SQS message with MessageId: "
                        + message.getMessageId(), e);
            }
        }

        return null;
    }
}
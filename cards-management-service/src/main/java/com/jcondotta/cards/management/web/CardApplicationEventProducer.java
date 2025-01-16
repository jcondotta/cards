package com.jcondotta.cards.management.web;

import com.jcondotta.cards.core.request.AddCardRequest;
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public interface CardApplicationEventProducer {

    @Queue(value = "${aws.sqs.queues.card-application-queue.name}")
    void send(@MessageBody AddCardRequest addCardRequest);
}

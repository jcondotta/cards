package com.blitzar.cards.listener;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

@Singleton
public class SqsClientCreatedEventListener implements BeanCreatedEventListener<SqsClient> {

    private static final Logger logger = LoggerFactory.getLogger(SqsClientCreatedEventListener.class);

    @Value("${aws.sqs.card-application-queue-name}")
    private String cardApplicationQueueName;

    @Override
    public SqsClient onCreated(@NonNull BeanCreatedEvent<SqsClient> event) {
        var sqsClient = event.getBean();
        var queueUrls = sqsClient.listQueues().queueUrls();

        if (queueUrls.stream().noneMatch(queueUrl -> queueUrl.contains(cardApplicationQueueName))) {
            logger.info("Queue not found. Creating SQS queue with name: {}", cardApplicationQueueName);
            try {
                var createQueueResponse = sqsClient.createQueue(builder -> builder.queueName(cardApplicationQueueName).build());
                logger.info("Queue created successfully: {} with ARN: {}", cardApplicationQueueName, createQueueResponse.queueUrl());
            }
            catch (Exception e) {
                logger.error("Failed to create SQS queue with name: {}", cardApplicationQueueName, e);
            }
        }
        else {
            logger.info("Queue already exists: {}", cardApplicationQueueName);
        }

        return sqsClient;
    }
}
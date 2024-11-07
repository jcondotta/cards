package com.blitzar.cards.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Map;

@Singleton
public class SQSQueueCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSQueueCreator.class);

    private final SqsClient sqsClient;

    @Inject
    public SQSQueueCreator(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String createSQSQueue(String queueName) {
        return createQueueIfNotExists(queueName, Map.of());
    }

    public String createSQSQueue(String queueName, Map<QueueAttributeName, String> queueAttributes) {
        return createQueueIfNotExists(queueName, queueAttributes);
    }

    protected String createQueueIfNotExists(String queueName, Map<QueueAttributeName, String> queueAttributes) {
        var queueUrls = sqsClient.listQueues().queueUrls();

        if (queueUrls.stream().noneMatch(queueUrl -> queueUrl.contains(queueName))) {
            LOGGER.info("Queue not found. Creating SQS queue with name: {}", queueName);
            return createQueue(queueName, queueAttributes);
        }
        else {
            LOGGER.debug("Queue exists: {}", queueName);
            return getQueueUrl(queueName);
        }
    }

    protected String createQueue(String queueName, Map<QueueAttributeName, String> queueAttributes) {
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(CreateQueueRequest.builder()
                .queueName(queueName)
                .attributes(queueAttributes)
                .build());

        LOGGER.info("Queue created successfully: {} with URL: {}", queueName, createQueueResponse.queueUrl());
        return createQueueResponse.queueUrl();
    }

    private String getQueueUrl(String queueName) {
        GetQueueUrlResponse queueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build());

        return queueUrlResponse.queueUrl();
    }
}
package com.jcondotta.cards.core.listener;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

import java.util.Map;

@Singleton
public class SqsClientCreatedEventListener implements BeanCreatedEventListener<SqsClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsClientCreatedEventListener.class);

    @Value("${aws.sqs.queues.card-application-queue.name}")
    private String cardApplicationQueueName;

    @Value("${aws.sqs.queues.card-application-dead-letter-queue.name}")
    private String cardApplicationDLQName;

    @Override
    public SqsClient onCreated(@NonNull BeanCreatedEvent<SqsClient> event) {
        var sqsClient = event.getBean();
        var sqsQueueCreator = new SQSQueueCreator(sqsClient);

        var cardApplicationQueueAttributes = Map.of(QueueAttributeName.VISIBILITY_TIMEOUT, "0");
        var cardApplicationQueueURL = sqsQueueCreator.createSQSQueue(cardApplicationQueueName, cardApplicationQueueAttributes);

        var cardApplicationDLQURL = sqsQueueCreator.createSQSQueue(cardApplicationDLQName);
        var cardApplicationDLQARN = getSingleQueueAttribute(sqsClient, cardApplicationDLQURL, QueueAttributeName.QUEUE_ARN);

        String redrivePolicy = String.format("{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":\"%d\"}", cardApplicationDLQARN, 1);

        sqsClient.setQueueAttributes(SetQueueAttributesRequest.builder()
                .queueUrl(cardApplicationQueueURL)
                .attributes(Map.of(QueueAttributeName.REDRIVE_POLICY, redrivePolicy))
                .build());

        return sqsClient;
    }

    private GetQueueAttributesResponse getQueueAttributes(SqsClient sqsClient, String sqsQueueURL, QueueAttributeName... queueAttributeNames) {
        return sqsClient.getQueueAttributes(builder -> builder.queueUrl(sqsQueueURL)
                .attributeNames(queueAttributeNames)
                .build());
    }

    private String getSingleQueueAttribute(SqsClient sqsClient, String sqsQueueURL, QueueAttributeName queueAttributeName) {
        var queueAttributesResponse = getQueueAttributes(sqsClient, sqsQueueURL, queueAttributeName);

        return queueAttributesResponse.attributes().get(queueAttributeName);
    }
}

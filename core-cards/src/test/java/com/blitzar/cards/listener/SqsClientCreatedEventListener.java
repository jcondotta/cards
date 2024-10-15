package com.blitzar.cards.listener;

import com.blitzar.cards.configuration.SqsConfiguration;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;
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

    private static final Logger logger = LoggerFactory.getLogger(SqsClientCreatedEventListener.class);

    @Inject
    private SqsConfiguration sqsConfiguration;

    @Value("${aws.sqs.card-application-queue.name}")
    private String cardApplicationQueueName;

    @Value("${aws.sqs.card-application-dlq.name}")
    private String cardApplicationDLQName;

    @Override
    public SqsClient onCreated(@NonNull BeanCreatedEvent<SqsClient> event) {

        System.out.println(sqsConfiguration.cardApplicationQueue().name());
        var sqsClient = event.getBean();
        var sqsQueueCreator = new SQSQueueCreator(sqsClient);

        var cardApplicationQueueAttributes = Map.of(QueueAttributeName.VISIBILITY_TIMEOUT, "0");
        var cardApplicationQueueURL = sqsQueueCreator.createSQSQueue(cardApplicationQueueName, cardApplicationQueueAttributes);

        var cardApplicationDLQURL = sqsQueueCreator.createSQSQueue(cardApplicationDLQName);
        var cardApplicationDLQARN = getSingleQueueAttribute(sqsClient, cardApplicationDLQURL, QueueAttributeName.QUEUE_ARN);

        System.out.println(cardApplicationDLQARN);

        String redrivePolicy = String.format("{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":\"%d\"}", cardApplicationDLQARN, 1);
//
//        // Set the redrive policy on the main queue
        sqsClient.setQueueAttributes(SetQueueAttributesRequest.builder()
                .queueUrl(cardApplicationQueueURL)
                .attributes(Map.of(QueueAttributeName.REDRIVE_POLICY, redrivePolicy))
                .build());
//
        System.out.println("Redrive policy set successfully on queue: " + cardApplicationQueueURL);


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

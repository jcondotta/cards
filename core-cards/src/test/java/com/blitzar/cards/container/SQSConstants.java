package com.blitzar.cards.container;

import org.testcontainers.containers.localstack.LocalStackContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service;

public class SQSConstants {

    public static class CardApplicationQueue {
        public static final String SQS_CARD_APPLICATION_QUEUE_NAME = "card-application-test";
        public static final String SQS_CARD_APPLICATION_VISIBILITY_TIMEOUT = "1";
        public static final String SQS_CARD_APPLICATION_RECEIVE_MESSAGE_WAIT_TIME_SECONDS = "20";
        public static final String SQS_CARD_APPLICATION_MESSAGE_RETENTION_PERIOD_SECONDS = "345600";  // 4 days

        public static Map.Entry<String, String>[] getQueuePropertyEntries() {
            return new Map.Entry[]{
                    Map.entry("AWS_SQS_CARD_APPLICATION_QUEUE_NAME", SQS_CARD_APPLICATION_QUEUE_NAME),
                    Map.entry("AWS_SQS_CARD_APPLICATION_VISIBILITY_TIMEOUT", SQS_CARD_APPLICATION_VISIBILITY_TIMEOUT),
                    Map.entry("AWS_SQS_CARD_APPLICATION_RECEIVE_MESSAGE_WAIT_TIME_SECONDS", SQS_CARD_APPLICATION_RECEIVE_MESSAGE_WAIT_TIME_SECONDS),
                    Map.entry("AWS_SQS_CARD_APPLICATION_MESSAGE_RETENTION_PERIOD_SECONDS", SQS_CARD_APPLICATION_MESSAGE_RETENTION_PERIOD_SECONDS)
            };
        }
    }

    public static class CardApplicationDLQ {
        public static final String SQS_CARD_APPLICATION_DLQ_NAME = "card-application-dlq-test";
        public static final String SQS_CARD_APPLICATION_DLQ_MESSAGE_RETENTION_PERIOD_SECONDS = "1209600";  // 14 days

        public static Map.Entry<String, String>[] getDLQPropertyEntries() {
            return new Map.Entry[]{
                    Map.entry("AWS_SQS_CARD_APPLICATION_DLQ_NAME", SQS_CARD_APPLICATION_DLQ_NAME),
                    Map.entry("AWS_SQS_CARD_APPLICATION_DLQ_MESSAGE_RETENTION_PERIOD_SECONDS", SQS_CARD_APPLICATION_DLQ_MESSAGE_RETENTION_PERIOD_SECONDS)
            };
        }
    }

    //TODO Organize it better
    public static Map.Entry<String, String>[] getSQSPropertyEntries(LocalStackContainer localStackContainer) {
        List<Map.Entry<String, String>> sqsProperties = new ArrayList<>();

        // Add the SQS endpoint
        sqsProperties.add(Map.entry("AWS_SQS_ENDPOINT", localStackContainer.getEndpointOverride(Service.SQS).toString()));

        // Add properties from CardApplicationQueue
        sqsProperties.addAll(List.of(CardApplicationQueue.getQueuePropertyEntries()));

        // Add properties from CardApplicationDLQ
        sqsProperties.addAll(List.of(CardApplicationDLQ.getDLQPropertyEntries()));

        // Convert the list back to a Map.Entry array
        return sqsProperties.toArray(new Map.Entry[0]);
    }

    public static String prettyPrint(LocalStackContainer localStackContainer) {
        return new StringBuilder()
                .append("SQS Card Application Queue:\n")
                .append(String.format("  SQS endpoint: %s%n", localStackContainer.getEndpointOverride(Service.SQS)))
                .append(String.format("  Queue name: %s%n", CardApplicationQueue.SQS_CARD_APPLICATION_QUEUE_NAME))
                .append(String.format("  Visibility timeout: %s seconds%n", CardApplicationQueue.SQS_CARD_APPLICATION_VISIBILITY_TIMEOUT))
                .append(String.format("  Receive message wait time: %s seconds%n", CardApplicationQueue.SQS_CARD_APPLICATION_RECEIVE_MESSAGE_WAIT_TIME_SECONDS))
                .append(String.format("  Message retention period: %s seconds%n", CardApplicationQueue.SQS_CARD_APPLICATION_MESSAGE_RETENTION_PERIOD_SECONDS))
                .append("\n")
                .append("SQS Card Application DLQ:\n")
                .append(String.format("  DLQ name: %s%n", CardApplicationDLQ.SQS_CARD_APPLICATION_DLQ_NAME))
                .append(String.format("  DLQ message retention period: %s seconds%n", CardApplicationDLQ.SQS_CARD_APPLICATION_DLQ_MESSAGE_RETENTION_PERIOD_SECONDS))
                .toString();
    }
}

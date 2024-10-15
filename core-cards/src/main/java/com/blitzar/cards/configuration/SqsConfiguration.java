package com.blitzar.cards.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.sqs")
public record SqsConfiguration(
        String endpoint,
        CardApplicationQueueConfiguration cardApplicationQueue,
        CardApplicationDlqConfiguration cardApplicationDlq
) {

    @ConfigurationProperties("card-application-queue")
    public record CardApplicationQueueConfiguration(
            String name,
            Integer visibilityTimeout,
            Integer receiveMessageWaitTimeSeconds,
            Integer messageRetentionPeriodSeconds
    ) {}

    @ConfigurationProperties("card-application-dlq")
    public record CardApplicationDlqConfiguration(
            String name,
            Integer messageRetentionPeriodSeconds
    ) {}
}
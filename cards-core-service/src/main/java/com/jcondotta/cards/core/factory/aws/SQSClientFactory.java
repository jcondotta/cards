package com.jcondotta.cards.core.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Factory
public class SQSClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(SQSClientFactory.class);

    @Singleton
    @Replaces(SqsClient.class)
    @Requires(missingProperty = "aws.sqs.endpoint")
    public SqsClient sqsClient(Region region) {
        var defaultCredentialsProvider = DefaultCredentialsProvider.create();

        logger.info("Building SQSClient for region: {}. Using credentials from: {}", region, defaultCredentialsProvider);

        return SqsClient.builder()
                .region(region)
                .credentialsProvider(defaultCredentialsProvider)
                .build();
    }
}
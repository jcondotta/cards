package com.jcondotta.cards.core.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Factory
public class DynamoDBClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBClientFactory.class);

    @Singleton
    @Replaces(DynamoDbClient.class)
    @Requires(missingProperty = "aws.dynamodb.endpoint")
    public DynamoDbClient dynamoDbClient(Region region) {
        var defaultCredentialsProvider = DefaultCredentialsProvider.create();

        logger.info("Building DynamoDbClient for region: {}. Using credentials from: {}", region, defaultCredentialsProvider);

        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(defaultCredentialsProvider)
                .build();
    }
}
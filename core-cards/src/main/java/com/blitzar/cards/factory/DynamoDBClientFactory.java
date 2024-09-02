package com.blitzar.cards.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Factory
public class DynamoDBClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBClientFactory.class);

    @Singleton
    @Replaces(DynamoDbClient.class)
    @Requires(property = "aws.dynamodb.endpoint", pattern = "^$")
    public DynamoDbClient dynamoDbClient(AwsCredentials awsCredentials, Region region){
        logger.info("Building DynamoDbClient with params: awsCredentials: {} and region: {}", awsCredentials, region);
        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    @Singleton
    @Replaces(DynamoDbClient.class)
    @Requires(property = "aws.dynamodb.endpoint", pattern = "(.|\\s)*\\S(.|\\s)*")
    public DynamoDbClient dynamoDbClientEndpointOverridden(AwsCredentials awsCredentials, Region region, @Value("${aws.sqs.endpoint}") String endpoint){
        logger.info("Building DynamoDbClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpoint);

        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
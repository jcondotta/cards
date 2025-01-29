package com.jcondotta.cards.core.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Factory
public class DynamoDBClientTestFactory {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBClientTestFactory.class);

    @Primary
    @Singleton
    @Requires(property = "aws.dynamodb.endpoint")
    public DynamoDbClient dynamoDbClient(AwsCredentials awsCredentials, Region region, @Value("${aws.dynamodb.endpoint}") String endpoint){
        logger.info("Building DynamoDbClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpoint);

        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
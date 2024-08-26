package com.blitzar.cards.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.util.StringUtils;
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

    @Property(name = "aws.dynamodb.endpoint", defaultValue = "")
    private String endpoint;

    @Singleton
    @Replaces(DynamoDbClient.class)
//    @Requires(property = "aws.dynamodb.endpoint")
    public DynamoDbClient dynamoDbClient(AwsCredentials awsCredentials, Region region){

        if(StringUtils.isEmpty(endpoint)){
            logger.info("Building DynamoDbClient with params: awsCredentials: {} and region: {}", awsCredentials, region);
            return DynamoDbClient.builder()
                    .region(region)
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build();
        }

        logger.info("Building DynamoDbClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpoint);
        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
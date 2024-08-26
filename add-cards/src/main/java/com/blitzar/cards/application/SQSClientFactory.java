package com.blitzar.cards.application;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Factory
public class SQSClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(SQSClientFactory.class);

    @Property(name = "aws.sqs.endpoint", defaultValue = "")
    private String endpoint;

    @Singleton
    @Replaces(SqsClient.class)
//    @Requires(property = "aws.dynamodb.endpoint")
    public SqsClient sqsClient(AwsCredentials awsCredentials, Region region){

        if(StringUtils.isEmpty(endpoint)){
            logger.info("Building SQSClient with params: awsCredentials: {} and region: {}", awsCredentials, region);
            return SqsClient.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
        }

        logger.info("Building SQSClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpoint);
        return SqsClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
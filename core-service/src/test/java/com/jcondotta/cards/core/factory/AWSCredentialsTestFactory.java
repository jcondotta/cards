package com.jcondotta.cards.core.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Factory
public class AWSCredentialsTestFactory {

    private static final Logger logger = LoggerFactory.getLogger(AWSCredentialsTestFactory.class);

    @Singleton
    @Requires(property = "aws.access-key-id")
    @Requires(property = "aws.secret-key")
    public AwsCredentials awsCredentials(@Value("${aws.access-key-id}") String accessKeyId, @Value("${aws.secret-key}") String secretKey) {
        logger.info("Configuring AWS credentials using accessKeyId: {} and secretKey: {}", accessKeyId, secretKey);
        return AwsBasicCredentials.create(accessKeyId, secretKey);
    }
}

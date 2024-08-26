package com.blitzar.cards.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Factory
public class AWSCredentialsFactory {

    @Singleton
    @Requires(property = "aws.access-key-id")
    @Requires(property = "aws.secret-key")
    public AwsCredentials awsCredentials(@Value("${aws.access-key-id}") String accessKey, @Value("${aws.secret-key}") String secretKey){
        return AwsBasicCredentials.create(accessKey, secretKey);
    }
}

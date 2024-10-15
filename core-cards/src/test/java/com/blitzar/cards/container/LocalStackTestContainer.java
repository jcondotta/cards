package com.blitzar.cards.container;

import io.micronaut.test.support.TestPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
public interface LocalStackTestContainer extends TestPropertyProvider {

    Logger logger = LoggerFactory.getLogger(LocalStackTestContainer.class);

    String LOCAL_STACK_IMAGE_NAME = "localstack/localstack:3.7.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCAL_STACK_IMAGE_NAME);

    String DYNAMODB_CARDS_TABLE_NAME = "cards-test";
    String DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME = "cards-by-bank-account-id-gsi-test";

    String SQS_CARD_APPLICATION_QUEUE_NAME = "card-application-test";
    String SQS_CARD_APPLICATION_VISIBILITY_TIMEOUT = "1";
    String SQS_CARD_APPLICATION_RECEIVE_MESSAGE_WAIT_TIME_SECONDS = "20";
    String SQS_CARD_APPLICATION_MESSAGE_RETENTION_PERIOD_SECONDS = "345600";  // 4 days

    String SQS_CARD_APPLICATION_DLQ_NAME = "card-application-dlq-test";
    String SQS_CARD_APPLICATION_DLQ_MESSAGE_RETENTION_PERIOD_SECONDS = "1209600";  // 14 days

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.DYNAMODB, Service.SQS, Service.LAMBDA)
            .withLogConsumer(outputFrame -> logger.info(outputFrame.getUtf8StringWithoutLineEnding()));

    @Override
    default Map<String, String> getProperties() {
        try {
            Startables.deepStart(LOCALSTACK_CONTAINER).join();
        }
        catch (Exception e) {
            logger.error("Failed to start LocalStack container: {}", e.getMessage());

            throw new RuntimeException("Failed to start LocalStack container", e);
        }

        logContainerConfiguration();

        return getAWSProperties();
    }

    default Map<String, String> getAWSProperties() {
        Map<String, String> awsProperties = new HashMap<>(Map.ofEntries(
                Map.entry("AWS_ACCESS_KEY_ID", LOCALSTACK_CONTAINER.getAccessKey()),
                Map.entry("AWS_SECRET_ACCESS_KEY", LOCALSTACK_CONTAINER.getSecretKey()),
                Map.entry("AWS_DEFAULT_REGION", LOCALSTACK_CONTAINER.getRegion())
        ));

        awsProperties.putAll(DynamoDBConstants.getDynamoDBProperties(LOCALSTACK_CONTAINER));
        awsProperties.putAll(SQSConstants.getSQSProperties(LOCALSTACK_CONTAINER));

        return awsProperties;
    }

    default void logContainerConfiguration() {
        StringBuilder sbConfig = new StringBuilder("\nLocalStack container configuration:\n")
                .append(String.format("  Access Key: %s%n", LOCALSTACK_CONTAINER.getAccessKey()))
                .append(String.format("  Secret Key: %s%n", LOCALSTACK_CONTAINER.getSecretKey()))
                .append(String.format("  Region: %s%n", LOCALSTACK_CONTAINER.getRegion()))
                .append(DynamoDBConstants.prettyPrint(LOCALSTACK_CONTAINER))
                .append(SQSConstants.prettyPrint(LOCALSTACK_CONTAINER));

        logger.info(sbConfig.toString());
    }
}
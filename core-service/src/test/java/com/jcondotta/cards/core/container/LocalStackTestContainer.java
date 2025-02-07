package com.jcondotta.cards.core.container;

import com.redis.testcontainers.RedisContainer;
import io.micronaut.test.support.TestPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Testcontainers
public interface LocalStackTestContainer extends TestPropertyProvider {

    Logger LOGGER = LoggerFactory.getLogger(LocalStackTestContainer.class);

    String LOCAL_STACK_IMAGE_NAME = "localstack/localstack:3.7.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCAL_STACK_IMAGE_NAME);

    String REDIS_IMAGE_NAME = "redis:7.2.4";
    DockerImageName REDIS_IMAGE = DockerImageName.parse(REDIS_IMAGE_NAME);

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.DYNAMODB, Service.SQS)
            .withLogConsumer(outputFrame -> LOGGER.debug(outputFrame.getUtf8StringWithoutLineEnding()));

    RedisContainer REDIS_CONTAINER = new RedisContainer(REDIS_IMAGE)
            .withLogConsumer(outputFrame -> LOGGER.debug(outputFrame.getUtf8StringWithoutLineEnding()));

    @Override
    default Map<String, String> getProperties() {
        try {
            Startables.deepStart(LOCALSTACK_CONTAINER, REDIS_CONTAINER).join();
        }
        catch (Exception e) {
            LOGGER.error("Failed to start LocalStack container: {}", e.getMessage());
            throw new RuntimeException("Failed to start LocalStack container", e);
        }

        logContainerConfiguration();

        return getAWSProperties();
    }

    default Map<String, String> getAWSProperties() {
        return Map.ofEntries(
                Map.entry("AWS_ACCESS_KEY_ID", LOCALSTACK_CONTAINER.getAccessKey()),
                Map.entry("AWS_SECRET_ACCESS_KEY", LOCALSTACK_CONTAINER.getSecretKey()),
                Map.entry("AWS_DEFAULT_REGION", LOCALSTACK_CONTAINER.getRegion()),
                Map.entry("AWS_DYNAMODB_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB).toString()),
                Map.entry("AWS_SQS_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SQS).toString()),
                Map.entry("REDIS_HOST", REDIS_CONTAINER.getRedisHost()),
                Map.entry("REDIS_PORT", String.valueOf(REDIS_CONTAINER.getRedisPort()))
        );
    }

    default void logContainerConfiguration() {
        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append("\nLocalStack container configuration:\n")
                .append(String.format("  Access Key: %s%n", LOCALSTACK_CONTAINER.getAccessKey()))
                .append(String.format("  Secret Key: %s%n", LOCALSTACK_CONTAINER.getSecretKey()))
                .append(String.format("  Region: %s%n", LOCALSTACK_CONTAINER.getRegion()))
                .append(String.format("  DynamoDB Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB)))
                .append(String.format("  SQS Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SQS)));

        sbConfig.append("\nRedis container configuration:\n")
                .append(String.format("  Host: %s%n", REDIS_CONTAINER.getRedisHost()))
                .append(String.format("  Port: %s%n", REDIS_CONTAINER.getRedisPort()));

        LOGGER.info(sbConfig.toString());
    }
}
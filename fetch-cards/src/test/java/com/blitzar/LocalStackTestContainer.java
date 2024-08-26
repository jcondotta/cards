package com.blitzar;

import io.micronaut.test.support.TestPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public interface LocalStackTestContainer extends TestPropertyProvider { //, BeforeAllCallback {

    Logger logger = LoggerFactory.getLogger(LocalStackTestContainer.class);

    String localStackImageName = "localstack/localstack:3.5.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(localStackImageName);

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.DYNAMODB)
            .withReuse(false);

    @Override
    default Map<String, String> getProperties() {
        Startables.deepStart(LOCALSTACK_CONTAINER).join();

        return Stream.of(getAWSProperties())
                .flatMap(property -> property.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    default Map<String, String> getAWSProperties() {
        return Map.of(
                "aws.access-key-id", LOCALSTACK_CONTAINER.getAccessKey(),
                "aws.secret-key", LOCALSTACK_CONTAINER.getSecretKey(),
                "aws.region", LOCALSTACK_CONTAINER.getRegion(),
                "aws.dynamodb.endpoint", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB).toString());
    }
}



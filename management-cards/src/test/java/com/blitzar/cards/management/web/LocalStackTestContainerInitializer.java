package com.blitzar.cards.management.web;

import io.micronaut.context.*;
import io.micronaut.context.event.BeanInitializedEventListener;
import io.micronaut.context.event.BeanInitializingEvent;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.InitializingBeanDefinition;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.atomic.AtomicInteger;

public class LocalStackTestContainerInitializer {


//    private static final Logger logger = LoggerFactory.getLogger(LocalStackTestContainerInitializer.class);
//
//    private static final String localStackImageName = "localstack/localstack:3.5.0";
//    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(localStackImageName);
//
//    private static final AtomicInteger counter = new AtomicInteger();
//
//    private static final LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
//            .withServices(LocalStackContainer.Service.DYNAMODB)
//            .withServices(LocalStackContainer.Service.SQS)
//            .withReuse(false);
//
//    @Override
//    public void beforeAll(ExtensionContext extensionContext) throws Exception {
//        logger.info("Creating localstack container: {}", LOCALSTACK_IMAGE);
//
//        Startables.deepStart(LOCALSTACK_CONTAINER).join();
//
//        logger.info("****** " + counter.addAndGet(1));
//
//        System.setProperty("aws.access-key-id", LOCALSTACK_CONTAINER.getAccessKey());
//        System.setProperty("aws.secret-key", LOCALSTACK_CONTAINER.getSecretKey());
//        System.setProperty("aws.region", LOCALSTACK_CONTAINER.getRegion());
//        System.setProperty("aws.dynamodb.endpoint", LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString());
//        System.setProperty("aws.sqs.endpoint", LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
//        System.setProperty("AWS_SQS_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
//    }

}

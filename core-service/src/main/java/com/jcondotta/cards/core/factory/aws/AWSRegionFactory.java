package com.jcondotta.cards.core.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

@Factory
public class AWSRegionFactory {

    private static final Logger logger = LoggerFactory.getLogger(AWSRegionFactory.class);

    @Singleton
    @Requires(property = "aws.region")
    public Region region(@Value("${aws.region}") String region){
        logger.info("Configuring AWS Region: {}", region);
        return Region.of(region);
    }
}
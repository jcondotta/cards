package com.jcondotta.cards.core.context;

import ch.qos.logback.classic.LoggerContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import org.slf4j.LoggerFactory;

@Context
public class LogbackApplicationNameConfig {

    private static final String APPLICATION_NAME_CONTEXT_PARAM = "applicationName";

    public LogbackApplicationNameConfig(@Value("${micronaut.application.name}") String applicationName) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.putProperty(APPLICATION_NAME_CONTEXT_PARAM, applicationName);
    }
}
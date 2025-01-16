package com.jcondotta.cards.core.argument_provider.security;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class SQLInjectionArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("SQL Injection - Single Quote", "' OR '1'='1'")),
                Arguments.of(Named.of("SQL Injection - Comment", "1; DROP TABLE users; --")),
                Arguments.of(Named.of("SQL Injection - OR Condition", "1 OR 1=1")),
                Arguments.of(Named.of("SQL Injection - Time Delay", "1; WAITFOR DELAY '00:00:10';"))
        );
    }
}
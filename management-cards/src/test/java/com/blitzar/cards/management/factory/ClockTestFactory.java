package com.blitzar.cards.management.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Factory
public class ClockTestFactory {

    public static final Clock textClockFixedInstant = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);

    @Singleton
    @Primary
    public Clock testClockFixedInstant(){
        return textClockFixedInstant;
    }
}
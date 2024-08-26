package com.blitzar.cards.management.factory;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Locale;

@Factory
public class MessageSourceFactory {

    @Singleton
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource("i18n/messages/messages", Locale.ENGLISH);
        return messageSource;
    }

    @Singleton
    @Named("exceptionMessageSource")
    public MessageSource exceptionMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource("i18n/exceptions/exceptions", Locale.ENGLISH);
        return messageSource;
    }
}

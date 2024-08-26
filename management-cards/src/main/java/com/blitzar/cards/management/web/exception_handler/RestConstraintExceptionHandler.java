package com.blitzar.cards.management.web.exception_handler;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

@Produces
@Singleton
@Replaces(value = ConstraintExceptionHandler.class)
@Requires(classes = {ConstraintViolationException.class, ExceptionHandler.class})
public class RestConstraintExceptionHandler extends ConstraintExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceNotFoundExceptionHandler.class);

    private final MessageSource messageSource;
    private final ErrorResponseProcessor<?> errorResponseProcessor;

    public RestConstraintExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        super(errorResponseProcessor);
        this.messageSource = messageSource;
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    @Status(value = HttpStatus.BAD_REQUEST)
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        var locale = (Locale) request.getLocale().orElse(Locale.getDefault());

        var errorMessage = messageSource.getMessage(exception.getMessage(), locale).orElse(exception.getMessage());

        logger.error(errorMessage);

        return errorResponseProcessor.processResponse(ErrorContext.builder(request)
                .cause(exception)
                .errorMessage(errorMessage)
                .build(), HttpResponse.badRequest());
    }
}

package com.jcondotta.cards.query.exception_handler;

import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import io.micronaut.context.MessageSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Singleton
public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLExceptionHandler.class);

    private final MessageSource messageSource;

    @Inject
    public GraphQLExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        var exception = handlerParameters.getException();
        var errorMessage = messageSource.getMessage(exception.getMessage(), Locale.getDefault())
                .orElse(exception.getMessage());

        var path = handlerParameters.getPath();
        var graphQLError = GraphqlErrorBuilder.newError()
                .message(String.format("Exception while fetching data (%s) : %s", path, errorMessage))
                .path(path)
                .location(handlerParameters.getSourceLocation())
                .build();

        logger.error(graphQLError.getMessage());
        return CompletableFuture.completedFuture(DataFetcherExceptionHandlerResult.newResult()
                .error(graphQLError)
                .build());
    }
}
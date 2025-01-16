package com.jcondotta.cards.query.factory;

import com.jcondotta.cards.query.service.CardFetcher;
import com.jcondotta.cards.query.service.CardsFetcher;
import com.jcondotta.cards.query.exception_handler.GraphQLExceptionHandler;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaMissingError;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Factory
public class GraphQLFactory {

    private static final String GRAPHQL_SCHEMA_FILE_NAME = "schema.graphqls";

    @Singleton
    @Named("graphQLSchemaInputStream")
    public InputStream graphQLSchemaInputStream(ResourceResolver resourceResolver){
        var graphQLSchemaFilePath = String.format("classpath:%s", GRAPHQL_SCHEMA_FILE_NAME);

        return resourceResolver.getResourceAsStream(graphQLSchemaFilePath)
                .orElseThrow(SchemaMissingError::new);
    }

    @Singleton
    public TypeDefinitionRegistry typeDefinitionRegistry(@Named("graphQLSchemaInputStream") InputStream graphQLSchemaInputStream){
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        return typeRegistry.merge(schemaParser.parse(new BufferedReader(new InputStreamReader(graphQLSchemaInputStream))));
    }

    @Singleton
    public RuntimeWiring runtimeWiring(CardFetcher cardFetcher, CardsFetcher cardsFetcher){
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("cards", cardsFetcher)
                        .dataFetcher("card", cardFetcher)
                ).build();
    }

    @Singleton
    public GraphQLSchema graphQLSchema(TypeDefinitionRegistry typeRegistry, RuntimeWiring runtimeWiring){
        return new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    @Singleton
    public GraphQL graphQL(GraphQLSchema graphQLSchema, GraphQLExceptionHandler graphQLExceptionHandler){
        return GraphQL.newGraphQL(graphQLSchema)
                .defaultDataFetcherExceptionHandler(graphQLExceptionHandler)
                .build();
    }
}

package com.blitzar.cards.management.web.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.management.factory.CardTestFactory;
import com.blitzar.cards.management.web.controller.ManagementCardAPIConstants;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class ActivateCardLambdaIT {

    private ApiGatewayProxyRequestEventFunction apiGatewayProxyRequestEventFunction;
    private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    DynamoDbTable<Card> dynamoDbTable;

    @MockBean(value = DynamoDbTable.class)
    DynamoDbTable<Card> dynamoDbTable() {
        return Mockito.mock(DynamoDbTable.class);
    }

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach() {
        apiGatewayProxyRequestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);

        apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.PATCH.name())
                .withPath(ManagementCardAPIConstants.MANAGEMENT_CARD_V1_MAPPING);
    }

//    @Test
//    public void givenExistentCardId_whenActivateCard_thenReturnOk() {
//        var card = CardTestFactory.buildCard(bankAccountId, cardholderName);
//
//        Mockito.when(dynamoDbTable.getItem(Mockito.any(Key.class))).thenReturn(card);
//
////        apiGatewayProxyRequestEvent.setPathParameters(Map.of("card-id", card.getCardId()));
//        apiGatewayProxyRequestEvent.setPath("/api/v1/cards/card-id/" + "123" + "/activation");
//        APIGatewayProxyResponseEvent responseEvent = apiGatewayProxyRequestEventFunction.handleRequest(apiGatewayProxyRequestEvent, new MockLambdaContext());
//
//        Assertions.assertThat(responseEvent.getStatusCode()).isEqualTo(HttpResponse.noContent().code());
//    }
}

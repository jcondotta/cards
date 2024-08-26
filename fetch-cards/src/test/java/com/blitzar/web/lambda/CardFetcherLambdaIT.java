package com.blitzar.web.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.blitzar.LocalStackTestContainer;
import com.blitzar.cards.domain.Card;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpMethod;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.net.http.HttpRequest;
import java.time.Clock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardFetcherLambdaIT implements LocalStackTestContainer{

    private ApiGatewayProxyRequestEventFunction apiGatewayProxyRequestEventFunction;
    private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    private String bankAccountId1 = "998372";
    private String cardholderName1 = "Jefferson Condotta";

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        apiGatewayProxyRequestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);

        apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent();
        apiGatewayProxyRequestEvent.setPath("/graphql");
        apiGatewayProxyRequestEvent.setHttpMethod(HttpMethod.POST.name());
    }

    @BeforeEach
    public void beforeEach() {
    }

    @Test
    public void givenExistentCardId_whenActivateCard_thenReturnOk() {

        String graphQLQuery = """
                { "query": "{ card: 123 }" } """;

        System.out.println(graphQLQuery);
        apiGatewayProxyRequestEvent.setBody(graphQLQuery);
        var response = apiGatewayProxyRequestEventFunction.handleRequest(apiGatewayProxyRequestEvent, new MockLambdaContext());
        System.out.println(response);

//        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
//        assertEquals("{\"message\":\"Hello World\"}",  response.getBody());


//        given()
//            .spec(requestSpecification)
//            .body(query)
//        .when()
//            .post()
//        .then()
//            .statusCode(HttpStatus.OK.getCode())
//                .and()
//            .rootPath("data")
//                .body("card", is(notNullValue()))
//                    .body("card.bankAccountId", equalTo(card1.getBankAccountId()))
//                    .body("card.cardholderName", equalTo(card1.getCardholderName()));
    }
}

package com.blitzar.cards.management.web.controller;

import com.blitzar.cards.management.InitializeLocalStackTestContainer;
import com.blitzar.cards.management.web.LocalStackTestContainer;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
@InitializeLocalStackTestContainer
class AddCardControllerIT implements LocalStackTestContainer {

    @Inject
    private SqsClient sqsClient;

    @Inject
    private JsonMapper jsonMapper;

    @Value("${aws.sqs.card-application-queue-name}")
    private String cardApplicationQueueName;

    private String cardApplicationQueueURL;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    private RequestSpecification requestSpecification;

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(ManagementCardAPIConstants.CARD_BASE_PATH_API_V1_MAPPING);
    }

    @PostConstruct
    protected void init(){
        this.cardApplicationQueueURL = sqsClient.createQueue(builder -> builder.queueName(cardApplicationQueueName).build()).queueUrl();
    }

    @Test
    public void givenExistentCardId_whenActivateCard_thenUpdateCardStatus(){
        var addCardRequest = new AddCardRequest(bankAccountId, cardholderName);

        given()
            .spec(requestSpecification)
            .body(addCardRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.getCode());

        await().pollDelay(1, TimeUnit.SECONDS).untilAsserted(() -> {
            var receiveMessageResponse = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationQueueURL).build());
            assertThat(receiveMessageResponse.messages().size()).isEqualTo(1);

            var message = receiveMessageResponse.messages().get(0);
            var messageAddCardRequest = jsonMapper.readValue(message.body(), AddCardRequest.class);

            assertThat(messageAddCardRequest.bankAccountId()).isEqualTo(addCardRequest.bankAccountId());
            assertThat(messageAddCardRequest.cardholderName()).isEqualTo(addCardRequest.cardholderName());
        });
    }


}
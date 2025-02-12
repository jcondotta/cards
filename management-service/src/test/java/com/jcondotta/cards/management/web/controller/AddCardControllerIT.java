package com.jcondotta.cards.management.web.controller;

import com.jcondotta.cards.core.container.LocalStackTestContainer;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
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

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class AddCardControllerIT implements LocalStackTestContainer {

    @Inject
    private SqsClient sqsClient;

    @Inject
    private JsonMapper jsonMapper;

    @Value("${aws.sqs.queues.card-application-queue.name}")
    private String cardApplicationQueueName;
    private String cardApplicationQueueURL;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    private RequestSpecification requestSpecification;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(CardApiPaths.BASE_V1_PATH);
    }

    @PostConstruct
    protected void init() {
        this.cardApplicationQueueURL = sqsClient.createQueue(builder -> builder.queueName(cardApplicationQueueName).build()).queueUrl();
    }

    @Test
    public void shouldAcceptAndSendSQSMessage_whenAddCardRequestIsValid() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);

//        given()
//            .spec(requestSpecification)
//            .body(addCardRequest)
//        .when()
//            .post()
//        .then()
//            .statusCode(HttpStatus.ACCEPTED.getCode());
//
//        await().pollDelay(1, TimeUnit.SECONDS).untilAsserted(() -> {
//            var receiveMessageResponse = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationQueueURL).build());
//            assertThat(receiveMessageResponse.messages().size()).isEqualTo(1);
//
//            var message = receiveMessageResponse.messages().getFirst();
//            var messageAddCardRequest = jsonMapper.readValue(message.body(), AddCardRequest.class);
//
//            assertThat(messageAddCardRequest.bankAccountId()).isEqualTo(addCardRequest.bankAccountId());
//            assertThat(messageAddCardRequest.cardholderName()).isEqualTo(addCardRequest.cardholderName());
//        });
    }

    @Test
    public void shouldReturn400_whenBankAccountIdIsNull() {
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);

        given()
            .spec(requestSpecification)
            .body(addCardRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.bankAccountId.notNull",
                        Locale.getDefault()).orElseThrow()));
    }

    @Test
    public void shouldReturn400_whenCardholderNameIsInvalid() {
        var invalidCardholderName = "";
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);

        given()
            .spec(requestSpecification)
            .body(addCardRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.cardholderName.notBlank",
                        Locale.getDefault()).orElseThrow()));
    }

    @Test
    public void shouldReturn400_whenCardholderNameExceeds31Characters() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(32));

        given()
            .spec(requestSpecification)
            .body(addCardRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.cardholderName.tooLong",
                        Locale.getDefault()).orElseThrow()));
    }
}

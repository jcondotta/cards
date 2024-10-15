package com.blitzar.cards.application.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.blitzar.cards.configuration.SqsConfiguration;
import com.blitzar.cards.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.cards.container.LocalStackTestContainer;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.helper.CardTablePurgeService;
import com.blitzar.cards.helper.TestBankAccount;
import com.blitzar.cards.helper.TestCardholder;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CardApplicationEventHandlerIT implements LocalStackTestContainer {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SqsClient sqsClient;

    @Inject
    private JsonMapper jsonMapper;

    @Inject
    private Clock clock;

    @Inject
    private DynamoDbTable<Card> dynamoDbTable;

    @Inject
    private CardTablePurgeService cardTablePurgeService;

    private CardApplicationEventHandler cardApplicationEventHandler;

    @Inject
    private SqsConfiguration sqsConfiguration;

    private String cardApplicationQueueURL;
    private String cardApplicationDLQURL;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    @BeforeAll
    void beforeAll(){
        this.cardApplicationEventHandler = new CardApplicationEventHandler(applicationContext);

        this.cardApplicationQueueURL = sqsClient.getQueueUrl(builder -> builder
                .queueName(sqsConfiguration.cardApplicationQueue().name())
                .build())
                .queueUrl();

        this.cardApplicationDLQURL = sqsClient.getQueueUrl(builder -> builder
                .queueName(sqsConfiguration.cardApplicationDlq().name())
                .build())
                .queueUrl();
    }

    @AfterEach
    void afterEach(){
        cardTablePurgeService.purgeTable();
    }

    @Test
    void shouldProcessCardSuccessfully_whenValidSQSEventIsReceived() throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
        SQSEvent sqsEvent = createSQSEvent(addCardRequest);

        cardApplicationEventHandler.execute(sqsEvent);

        var cards = dynamoDbTable.scan().items()
                .stream()
                .toList();

        assertThat(cards).hasSize(1);

        cards.iterator().forEachRemaining(card -> assertAll(
                () -> assertThat(card.getCardId()).isNotNull(),
                () -> assertThat(card.getBankAccountId()).isEqualTo(addCardRequest.bankAccountId()),
                () -> assertThat(card.getCardholderName()).isEqualTo(addCardRequest.cardholderName()),
                () -> assertThat(card.getCardNumber()).isNotNull(),
                () -> assertThat(card.getCardStatus()).isEqualTo(AddCardRequest.DEFAULT_CARD_STATUS),
                () -> assertThat(card.getDailyWithdrawalLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_WITHDRAWAL_LIMIT),
                () -> assertThat(card.getDailyPaymentLimit()).isEqualTo(AddCardRequest.DEFAULT_DAILY_PAYMENT_LIMIT),
                () -> assertThat(card.getCreatedAt()).isEqualTo(LocalDateTime.now(clock)),
                () -> assertThat(card.getExpirationDate()).isEqualTo(LocalDateTime.now(clock)
                        .plus(AddCardRequest.DEFAULT_YEAR_PERIOD_EXPIRATION_DATE, ChronoUnit.YEARS))
        ));
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNullInSQSEvent() {
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });

        assertNoCardsSaved();
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenCardholderNameIsBlank(String invalidCardholderName) throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        assertNoCardsSaved();
    }

//    @ParameterizedTest
//    @ArgumentsSource(ThreatInputArgumentProvider.class)
//    void shouldThrowConstraintViolationException_whenCardholderNameIsMalicious(String invalidCardholderName) throws IOException {
//        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);
//        var sqsEvent = createSQSEvent(addCardRequest);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));
//
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.invalid");
//                    assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName");
//                });
//
//        assertNoCardsSaved();
//    }

    @Test
    void shouldThrowConstraintViolationException_whenCardholderNameExceeds21CharactersInSQSEvent() throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(22));
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.length.limit");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        assertNoCardsSaved();
    }

    @Test
    void shouldMoveMessageToDeadLetterQueue_whenProcessingFailsDueToNullBankAccountId() {
        var invalidAddCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);
        sendMessageToSQSQueue(cardApplicationQueueURL, invalidAddCardRequest);

        var cardApplicationMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationQueueURL).build()).messages();
        assertThat(cardApplicationMessages)
                .hasSize(1)
                .first()
                .satisfies(message -> {
                    var receivedAddCardRequest = extractAddCardRequest(message.body());
                    assertThat(receivedAddCardRequest.bankAccountId()).isNull();
                    assertThat(receivedAddCardRequest.cardholderName()).isEqualTo(CARDHOLDER_JEFFERSON);
                });

        var sqsEvent = createSQSEvent(invalidAddCardRequest);
        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });

        waitForNoMessagesInQueue(cardApplicationQueueURL);

        await().atMost(2, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var cardApplicationDLQMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationDLQURL).build()).messages();
                    assertThat(cardApplicationDLQMessages)
                            .hasSize(1)
                            .first()
                            .satisfies(message -> {
                                var receivedAddCardRequest = extractAddCardRequest(message.body());
                                assertThat(receivedAddCardRequest.bankAccountId()).isNull();
                                assertThat(receivedAddCardRequest.cardholderName()).isEqualTo(CARDHOLDER_JEFFERSON);
                            });
                });
    }

    @Test
    void shouldMoveMessageToDeadLetterQueue_whenProcessingFailsDueToNullCardholderName() {
        var invalidAddCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, null);
        sendMessageToSQSQueue(cardApplicationQueueURL, invalidAddCardRequest);

        var cardApplicationMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationQueueURL).build()).messages();
        assertThat(cardApplicationMessages)
                .hasSize(1)
                .first()
                .satisfies(message -> {
                    var receivedAddCardRequest = extractAddCardRequest(message.body());
                    assertThat(receivedAddCardRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
                    assertThat(receivedAddCardRequest.cardholderName()).isNull();
                });

        var sqsEvent = createSQSEvent(invalidAddCardRequest);
        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        waitForNoMessagesInQueue(cardApplicationQueueURL);

        await().atMost(2, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var cardApplicationDLQMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationDLQURL).build()).messages();
                    assertThat(cardApplicationDLQMessages)
                            .hasSize(1)
                            .first()
                            .satisfies(message -> {
                                var receivedAddCardRequest = extractAddCardRequest(message.body());
                                assertThat(receivedAddCardRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
                                assertThat(receivedAddCardRequest.cardholderName()).isNull();
                            });
                });
    }

    private void waitForNoMessagesInQueue(String queueUrl) {
        await().atMost(2, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var messages = sqsClient.receiveMessage(builder -> builder.queueUrl(queueUrl).build()).messages();
                    assertThat(messages).hasSize(0);
                });
    }

    private String serializeToJson(Object message) {
        try {
            return jsonMapper.writeValueAsString(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    private void sendMessageToSQSQueue(String sqsQueueURL, Object message) {
        var sqsMessageBody = serializeToJson(message);
        sqsClient.sendMessage(builder -> builder.queueUrl(sqsQueueURL).messageBody(sqsMessageBody));
    }

    private SQSEvent createSQSEvent(AddCardRequest addCardRequest) {
        var messageBody = serializeToJson(addCardRequest);

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(Collections.singletonList(message));

        return sqsEvent;
    }

    private AddCardRequest extractAddCardRequest(String messageBody) {
        try {
            return jsonMapper.readValue(messageBody, AddCardRequest.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize AddCardRequest from message body", e);
        }
    }

    private void assertNoCardsSaved() {
        assertThat(dynamoDbTable.scan().items()
                .stream()
                .count()).isZero();
    }
}

package com.jcondotta.cards.process.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.jcondotta.cards.core.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.cards.core.argument_provider.security.ThreatInputArgumentProvider;
import com.jcondotta.cards.core.container.LocalStackTestContainer;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.helper.CardTablePurgeService;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import com.jcondotta.cards.core.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Value;
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

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_NAME_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    @Inject
    ApplicationContext applicationContext;

    @Inject
    SqsClient sqsClient;

    @Inject
    JsonMapper jsonMapper;

    @Inject
    Clock clock;

    @Inject
    DynamoDbTable<Card> dynamoDbTable;

    @Inject
    CardTablePurgeService cardTablePurgeService;

    CardApplicationEventHandler cardApplicationEventHandler;

    @Value("${aws.sqs.queues.card-application-queue.name}")
    String cardApplicationQueueName;
    String cardApplicationQueueURL;

    @Value("${aws.sqs.queues.card-application-dead-letter-queue.name}")
    String cardApplicationDLQName;
    String cardApplicationDLQURL;

    @BeforeAll
    void beforeAll(){
        this.cardApplicationEventHandler = new CardApplicationEventHandler(applicationContext);

        this.cardApplicationQueueURL = sqsClient.getQueueUrl(builder -> builder
                .queueName(cardApplicationQueueName)
                .build())
                .queueUrl();

        this.cardApplicationDLQURL = sqsClient.getQueueUrl(builder -> builder
                .queueName(cardApplicationDLQName)
                .build())
                .queueUrl();
    }

    @AfterEach
    void afterEach(){
        cardTablePurgeService.purgeTable();
    }

    @Test
    void shouldProcessCardSuccessfully_whenValidSQSEventIsReceived() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_NAME_JEFFERSON);
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
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_NAME_JEFFERSON);
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
    void shouldThrowConstraintViolationException_whenCardholderNameIsBlank(String invalidCardholderName) {
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

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenCardholderNameIsMalicious(String invalidCardholderName) {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .anySatisfy(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        assertNoCardsSaved();
    }

    @Test
    void shouldThrowConstraintViolationException_whenCardholderNameExceeds32CharactersInSQSEvent() {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(32));
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("cardholderName");
                });

        assertNoCardsSaved();
    }

    @Test
    void shouldMoveMessageToDeadLetterQueue_whenProcessingFailsDueToNullBankAccountId() {
        var invalidAddCardRequest = new AddCardRequest(null, CARDHOLDER_NAME_JEFFERSON);
        sendMessageToSQSQueue(cardApplicationQueueURL, invalidAddCardRequest);

        var cardApplicationMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationQueueURL).build()).messages();
        assertThat(cardApplicationMessages)
                .hasSize(1)
                .first()
                .satisfies(message -> {
                    var receivedAddCardRequest = extractAddCardRequest(message.body());
                    assertThat(receivedAddCardRequest.bankAccountId()).isNull();
                    assertThat(receivedAddCardRequest.cardholderName()).isEqualTo(CARDHOLDER_NAME_JEFFERSON);
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

        var cardApplicationDLQMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationDLQURL).build()).messages();
        assertThat(cardApplicationDLQMessages)
                .hasSize(1)
                .first()
                .satisfies(message -> {
                    var receivedAddCardRequest = extractAddCardRequest(message.body());
                    assertThat(receivedAddCardRequest.bankAccountId()).isNull();
                    assertThat(receivedAddCardRequest.cardholderName()).isEqualTo(CARDHOLDER_NAME_JEFFERSON);
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

        var cardApplicationDLQMessages = sqsClient.receiveMessage(builder -> builder.queueUrl(cardApplicationDLQURL).build()).messages();
        assertThat(cardApplicationDLQMessages)
                .hasSize(1)
                .first()
                .satisfies(message -> {
                    var receivedAddCardRequest = extractAddCardRequest(message.body());
                    assertThat(receivedAddCardRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
                    assertThat(receivedAddCardRequest.cardholderName()).isNull();
                });
    }

    private void waitForNoMessagesInQueue(String queueUrl) {
        await().atMost(1, TimeUnit.SECONDS)
                .pollDelay(200, TimeUnit.MILLISECONDS)
                .pollInterval(300, TimeUnit.MILLISECONDS)
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

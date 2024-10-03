package com.blitzar.cards.application.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.blitzar.cards.application.LocalStackTestContainer;
import com.blitzar.cards.application.helper.TestBankAccount;
import com.blitzar.cards.application.service.TestCardholder;
import com.blitzar.cards.arguments.InvalidStringArgumentProvider;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
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

    private CardApplicationEventHandler cardApplicationEventHandler;

    @Value("${aws.sqs.card-application-queue-name}")
    private String cardApplicationQueueName;

    private String cardApplicationQueueURL;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    @BeforeAll
    protected void beforeAll(){
        this.cardApplicationEventHandler = new CardApplicationEventHandler(applicationContext);
        this.cardApplicationQueueURL = sqsClient.getQueueUrl(builder -> builder.queueName(cardApplicationQueueName).build()).queueUrl();
    }

    @AfterEach
    protected void afterEach(){
        dynamoDbTable.scan()
                .items()
                .stream()
                .forEach(card -> dynamoDbTable.deleteItem(card));
    }

    @Test
    public void shouldProcessCardSuccessfully_whenValidSQSEventReceived() throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
        SQSEvent sqsEvent = createSQSEvent(addCardRequest);

        cardApplicationEventHandler.execute(sqsEvent);

        var cards = dynamoDbTable.scan().items()
                .stream()
                .toList();

        assertThat(cards).hasSize(1);
        var card = cards.iterator().next();

        assertAll(
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
        );
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNullInSQSEvent() throws IOException {
        var addCardRequest = new AddCardRequest(null, CARDHOLDER_JEFFERSON);
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("bankAccountId");
                });

        assertNoCardsSaved();
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenCardholderNameIsInvalid(String invalidCardholderName) throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, invalidCardholderName);
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.notBlank");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName");
                });

        assertNoCardsSaved();
    }

    @Test
    public void shouldThrowConstraintViolationException_whenCardholderNameExceeds21CharactersInSQSEvent() throws IOException {
        var addCardRequest = new AddCardRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(22));
        var sqsEvent = createSQSEvent(addCardRequest);

        var exception = assertThrows(ConstraintViolationException.class, () -> cardApplicationEventHandler.execute(sqsEvent));

        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("card.cardholderName.length.limit");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName");
                });

        assertNoCardsSaved();
    }

    private SQSEvent createSQSEvent(AddCardRequest addCardRequest) throws IOException {
        var messageBody = jsonMapper.writeValueAsString(addCardRequest);

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(Collections.singletonList(message));

        return sqsEvent;
    }

    private void assertNoCardsSaved() {
        assertThat(dynamoDbTable.scan().items()
                .stream()
                .count()).isZero();
    }
}

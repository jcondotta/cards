package com.blitzar.cards.application.web.events;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.blitzar.cards.application.AddCardsApplication;
import com.blitzar.cards.application.LocalStackTestContainer;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.exception.ResourceNotFoundException;
import com.blitzar.cards.web.events.request.AddCardRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.json.JsonMapper;
import io.micronaut.serde.annotation.SerdeImport;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

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

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @PostConstruct
    protected void init(){
        this.cardApplicationEventHandler = new CardApplicationEventHandler(applicationContext);
        this.cardApplicationQueueURL = sqsClient.getQueueUrl(builder -> builder.queueName(cardApplicationQueueName).build()).queueUrl();
    }

    @Test
    public void givenValidSQSEvent_whenAddCard_thenSuccess() throws IOException {
        var addCardRequest = new AddCardRequest(bankAccountId, cardholderName);
        var messageBody = jsonMapper.writeValueAsString(addCardRequest);

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(Collections.singletonList(message));

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
}
package com.jcondotta.cards.query.service;

import com.jcondotta.cards.core.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.CardTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCardholder;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class CardFetcherTest {

    @InjectMocks
    private CardFetcher cardFetcher;

    @Mock
    private DynamoDbTable<Card> dynamoDbTableMock;

    @Mock
    private DataFetchingEnvironment fetchingEnvironmentMock;

    private static final UUID CARD_ID = UUID.fromString("d17fa947-3044-44e0-8f95-641a170908e6");
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = TestCardholder.JEFFERSON.getCardholderName();

    @Test
    public void givenExistentCardId_whenFetchCard_thenReturnCard() throws Exception {
        Card card = CardTestFactory.buildCard(CARD_ID, BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);

        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(card);
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(CARD_ID.toString());

        assertThat(cardFetcher.get(fetchingEnvironmentMock))
                .satisfies(cardDTO -> assertAll(
                        () -> assertThat(cardDTO.cardId()).isEqualTo(card.getCardId()),
                        () -> assertThat(cardDTO.bankAccountId()).isEqualTo(card.getBankAccountId()),
                        () -> assertThat(cardDTO.cardholderName()).isEqualTo(card.getCardholderName()),
                        () -> assertThat(cardDTO.cardNumber()).isEqualTo(card.getCardNumber()),
                        () -> assertThat(cardDTO.cardStatus()).isEqualTo(card.getCardStatus()),
                        () -> assertThat(cardDTO.dailyWithdrawalLimit()).isEqualTo(card.getDailyWithdrawalLimit()),
                        () -> assertThat(cardDTO.dailyPaymentLimit()).isEqualTo(card.getDailyPaymentLimit()),
                        () -> assertThat(cardDTO.createdAt()).isEqualTo(card.getCreatedAt()),
                        () -> assertThat(cardDTO.expirationDate()).isEqualTo(card.getExpirationDate())
                ));

        verify(dynamoDbTableMock).getItem(any(Key.class));
    }

    @Test
    public void givenNonExistentCardId_whenFetchCard_thenThrowException() {
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(CARD_ID.toString());
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(null);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.notFound");

        verify(dynamoDbTableMock).getItem(any(Key.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void givenInvalidCardId_whenFetchCard_thenThrowException(String invalidCardId) {
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(invalidCardId);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.cardId.notBlank");

        verify(dynamoDbTableMock, never()).getItem(any(Key.class));
    }
}

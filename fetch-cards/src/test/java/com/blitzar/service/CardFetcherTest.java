package com.blitzar.service;

import com.blitzar.InvalidStringArgumentProvider;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.fetch.service.CardFetcher;
import com.blitzar.cards.fetch.service.dto.CardDTO;
import com.blitzar.factory.CardTestFactory;
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

    private String cardId = "d17fa947-3044-44e0-8f95-641a170908e6";
    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @Test
    public void givenExistentCardId_whenFetchCard_thenReturnCard() throws Exception {
        Card card = CardTestFactory.buildCard(cardId, bankAccountId, cardholderName);

        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(card);
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(cardId);

        CardDTO cardDTO = cardFetcher.get(fetchingEnvironmentMock);
        verify(dynamoDbTableMock).getItem(any(Key.class));

        assertAll(
                () -> assertThat(cardDTO.cardId()).isEqualTo(card.getCardId()),
                () -> assertThat(cardDTO.bankAccountId()).isEqualTo(card.getBankAccountId()),
                () -> assertThat(cardDTO.cardholderName()).isEqualTo(card.getCardholderName()),
                () -> assertThat(cardDTO.cardNumber()).isEqualTo(card.getCardNumber()),
                () -> assertThat(cardDTO.cardStatus()).isEqualTo(card.getCardStatus()),
                () -> assertThat(cardDTO.dailyWithdrawalLimit()).isEqualTo(card.getDailyWithdrawalLimit()),
                () -> assertThat(cardDTO.dailyPaymentLimit()).isEqualTo(card.getDailyPaymentLimit()),
                () -> assertThat(cardDTO.createdAt()).isEqualTo(card.getCreatedAt()),
                () -> assertThat(cardDTO.expirationDate()).isEqualTo(card.getExpirationDate())
        );
    }

    @Test
    public void givenNonExistentCardId_whenFetchCard_thenThrowException() throws Exception {
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(cardId);
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(null);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.notFound");

        verify(dynamoDbTableMock).getItem(any(Key.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidCardId_whenFetchCard_thenThrowException(String invalidCardId) throws Exception {
        when(fetchingEnvironmentMock.getArgument("cardId")).thenReturn(invalidCardId);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.cardId.notBlank");

        verify(dynamoDbTableMock, never()).getItem(any(Key.class));
    }
}

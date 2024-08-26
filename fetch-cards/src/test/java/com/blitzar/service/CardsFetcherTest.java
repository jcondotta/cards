package com.blitzar.service;

import com.blitzar.InvalidStringArgumentProvider;
import com.blitzar.cards.domain.Card;
import com.blitzar.cards.fetch.service.CardsFetcher;
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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class CardsFetcherTest {

    @InjectMocks
    private CardsFetcher cardsFetcher;

    @Mock
    private DynamoDbIndex<Card> dynamoDbIndexMock;

    @Mock
    private PageIterable<Card> pageIterableMock;

    @Mock
    private DataFetchingEnvironment fetchingEnvironmentMock;

    private String cardId = "d17fa947-3044-44e0-8f95-641a170908e6";
    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @Test
    public void givenValidBankAccountId_whenFetchCards_thenSuccess() throws Exception {
        Card card = CardTestFactory.buildCard(cardId, bankAccountId, cardholderName);

        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(bankAccountId);
        when(pageIterableMock.stream()).thenReturn(Stream.of(Page.builder(Card.class).items(List.of(card)).build()));
        when(dynamoDbIndexMock.query(any(QueryConditional.class))).thenReturn(pageIterableMock);

        var cardDTOS = cardsFetcher.get(fetchingEnvironmentMock);
        var cardDTO = cardDTOS.get(0);

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

        verify(dynamoDbIndexMock).query(any(QueryConditional.class));
    }

    @Test
    public void givenNonExistentBankAccountId_whenFetchCards_thenThrowException() throws Exception {
        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(bankAccountId);
        when(pageIterableMock.stream()).thenReturn(Stream.empty());
        when(dynamoDbIndexMock.query(any(QueryConditional.class))).thenReturn(pageIterableMock);

        List<CardDTO> cardDTOS = cardsFetcher.get(fetchingEnvironmentMock);
        assertThat(cardDTOS).hasSize(0);

        verify(dynamoDbIndexMock).query(any(QueryConditional.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidBankAccountId_whenFetchCards_thenThrowException(String bankAccountId) throws Exception {
        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(bankAccountId);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardsFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.bankAccountId.notBlank");

        verify(dynamoDbIndexMock, never()).query(any(QueryConditional.class));
    }
}

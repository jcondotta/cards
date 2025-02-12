package com.jcondotta.cards.query.service;

import com.jcondotta.cards.core.argument_provider.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.factory.CardTestFactory;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CardsCacheService;
import com.jcondotta.cards.core.service.cache.WriteAsyncCacheService;
import com.jcondotta.cards.core.service.dto.CardDTO;
import com.jcondotta.cards.core.service.dto.CardsDTO;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.UUID;
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
    private CardsCacheService<CardsDTO> cardsCacheService;

    @Mock
    private SdkIterable<Page<Card>> sdkIterableMock;

    @Mock
    private DataFetchingEnvironment fetchingEnvironmentMock;

    private static final UUID CARD_ID = UUID.fromString("d17fa947-3044-44e0-8f95-641a170908e6");
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = "Jefferson Condotta";

    @Test
    public void givenValidBankAccountId_whenFetchCards_thenSuccess() throws Exception {
        Card card = CardTestFactory.buildCard(CARD_ID, BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);

        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(BANK_ACCOUNT_ID_BRAZIL.toString());

        when(dynamoDbIndexMock.query(any(QueryEnhancedRequest.class))).thenReturn(sdkIterableMock);
        when(sdkIterableMock.stream())
                .thenReturn(Stream.of(
                        Page.builder(Card.class).items(List.of(card)).build())
                );

        var cardDTOs = cardsFetcher.get(fetchingEnvironmentMock);
        assertThat(cardDTOs)
                .first()
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

        verify(dynamoDbIndexMock).query(any(QueryEnhancedRequest.class));

        var cacheKey = new BankAccountIdCacheKey(BANK_ACCOUNT_ID_BRAZIL);
        var cardsDTO = new CardsDTO(cardDTOs);
        verify(cardsCacheService).setCacheEntry(eq(cacheKey), eq(cardsDTO));
    }

    @Test
    public void givenNonExistentBankAccountId_whenFetchCards_thenThrowException() throws Exception {
        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(BANK_ACCOUNT_ID_BRAZIL.toString());

        when(dynamoDbIndexMock.query(any(QueryEnhancedRequest.class))).thenReturn(sdkIterableMock);
        when(sdkIterableMock.stream()).thenReturn(Stream.empty());

        List<CardDTO> cardDTOS = cardsFetcher.get(fetchingEnvironmentMock);
        assertThat(cardDTOS).hasSize(0);

        verify(dynamoDbIndexMock).query(any(QueryEnhancedRequest.class));
        verify(cardsCacheService).setCacheEntry(any(BankAccountIdCacheKey.class), any(CardsDTO.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void givenInvalidBankAccountId_whenFetchCards_thenThrowException(String bankAccountId) {
        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(bankAccountId);

        var exception = assertThrowsExactly(GraphQLException.class, () -> cardsFetcher.get(fetchingEnvironmentMock));
        assertThat(exception.getMessage()).isEqualTo("card.bankAccountId.notBlank");

        verify(dynamoDbIndexMock, never()).query(any(QueryEnhancedRequest.class));
        verify(cardsCacheService, never()).setCacheEntry(any(BankAccountIdCacheKey.class), any(CardsDTO.class));
    }
}

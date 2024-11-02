package com.blitzar.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.fetch.service.CardsFetcher;
import com.blitzar.cards.helper.TestBankAccount;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;

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

    private static final UUID CARD_ID = UUID.fromString("d17fa947-3044-44e0-8f95-641a170908e6");
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String CARDHOLDER_JEFFERSON = "Jefferson Condotta";

//    @Test
//    public void givenValidBankAccountId_whenFetchCards_thenSuccess() throws Exception {
//        Card card = CardTestFactory.buildCard(CARD_ID, BANK_ACCOUNT_ID_BRAZIL, CARDHOLDER_JEFFERSON);
//
//        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
//        when(pageIterableMock.stream()).thenReturn(Stream.of(Page.builder(Card.class).items(List.of(card)).build()));
//        when(dynamoDbIndexMock.query(any(QueryConditional.class))).thenReturn(pageIterableMock);
//
//        var cardDTOS = cardsFetcher.get(fetchingEnvironmentMock);
//        var cardDTO = cardDTOS.get(0);
//
//        assertAll(
//                () -> assertThat(cardDTO.cardId()).isEqualTo(card.getCardId()),
//                () -> assertThat(cardDTO.bankAccountId()).isEqualTo(card.getBankAccountId()),
//                () -> assertThat(cardDTO.cardholderName()).isEqualTo(card.getCardholderName()),
//                () -> assertThat(cardDTO.cardNumber()).isEqualTo(card.getCardNumber()),
//                () -> assertThat(cardDTO.cardStatus()).isEqualTo(card.getCardStatus()),
//                () -> assertThat(cardDTO.dailyWithdrawalLimit()).isEqualTo(card.getDailyWithdrawalLimit()),
//                () -> assertThat(cardDTO.dailyPaymentLimit()).isEqualTo(card.getDailyPaymentLimit()),
//                () -> assertThat(cardDTO.createdAt()).isEqualTo(card.getCreatedAt()),
//                () -> assertThat(cardDTO.expirationDate()).isEqualTo(card.getExpirationDate())
//        );
//
//        verify(dynamoDbIndexMock).query(any(QueryConditional.class));
//    }
//
//    @Test
//    public void givenNonExistentBankAccountId_whenFetchCards_thenThrowException() throws Exception {
//        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
//        when(pageIterableMock.stream()).thenReturn(Stream.empty());
//        when(dynamoDbIndexMock.query(any(QueryConditional.class))).thenReturn(pageIterableMock);
//
//        List<CardDTO> cardDTOS = cardsFetcher.get(fetchingEnvironmentMock);
//        assertThat(cardDTOS).hasSize(0);
//
//        verify(dynamoDbIndexMock).query(any(QueryConditional.class));
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(InvalidStringArgumentProvider.class)
//    public void givenInvalidBankAccountId_whenFetchCards_thenThrowException(String bankAccountId) throws Exception {
//        when(fetchingEnvironmentMock.getArgument("bankAccountId")).thenReturn(bankAccountId);
//
//        var exception = assertThrowsExactly(GraphQLException.class, () -> cardsFetcher.get(fetchingEnvironmentMock));
//        assertThat(exception.getMessage()).isEqualTo("card.bankAccountId.notBlank");
//
//        verify(dynamoDbIndexMock, never()).query(any(QueryConditional.class));
//    }
}

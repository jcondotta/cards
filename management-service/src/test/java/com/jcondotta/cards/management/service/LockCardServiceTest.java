package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.exception.ResourceNotFoundException;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCard;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CacheEvictionService;
import com.jcondotta.cards.core.service.dto.CardsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class LockCardServiceTest {

    private LockCardService lockCardService;

    @Mock
    private DynamoDbTable<Card> dynamoDbTableMock;

    @Mock
    private CacheEvictionService<CardsDTO> cacheEvictionService;

    @Mock
    private Card cardMock;

    private static final UUID CARD_ID_JEFFERSON = TestCard.JEFFERSON.getCardId();
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @BeforeEach
    public void beforeEach(){
        lockCardService = new LockCardService(dynamoDbTableMock, cacheEvictionService);
    }

    @Test
    public void givenExistentCardId_whenLockCard_thenUpdateCardStatus(){
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(cardMock);
        when(cardMock.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);

        lockCardService.lockCard(CARD_ID_JEFFERSON);

        verify(cardMock).setCardStatus(CardStatus.LOCKED);
        verify(dynamoDbTableMock).putItem(cardMock);
        verify(cacheEvictionService).evictCacheEntry(any(BankAccountIdCacheKey.class));
    }

    @Test
    public void givenNonExistentCardId_whenLockCard_thenReturnNotFound(){
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(null);

        var nonExistentCardId = UUID.randomUUID();
        var exception = assertThrowsExactly(ResourceNotFoundException.class, () -> lockCardService.lockCard(nonExistentCardId));

        assertAll(
                () -> assertThat(exception.getMessage()).isEqualTo("card.notFound"),
                () -> assertThat(exception.getRejectedIdentifier()).isEqualTo(nonExistentCardId)
        );

        verify(dynamoDbTableMock, never()).deleteItem(any(Card.class));
        verify(cacheEvictionService, never()).evictCacheEntry(any(BankAccountIdCacheKey.class));
    }
}
package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.exception.ResourceNotFoundException;
import com.jcondotta.cards.core.helper.TestBankAccount;
import com.jcondotta.cards.core.helper.TestCard;
import com.jcondotta.cards.core.service.cache.BankAccountIdCacheKey;
import com.jcondotta.cards.core.service.cache.CacheEvictionService;
import com.jcondotta.cards.core.service.dto.CardsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ActivateCardServiceTest {

    @InjectMocks
    private ActivateCardService activateCardService;

    @Mock
    private DynamoDbTable<Card> dynamoDbTableMock;

    @Mock
    private CacheEvictionService<CardsDTO> cacheEvictionService;

    @Mock
    private Card cardMock;

    private static final UUID CARD_ID_JEFFERSON = TestCard.JEFFERSON.getCardId();
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @Test
    public void shouldActivateCard_whenCardIdExists() {
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(cardMock);
        when(cardMock.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);

        activateCardService.activateCard(CARD_ID_JEFFERSON);

        verify(cardMock).setCardStatus(CardStatus.ACTIVE);
        verify(dynamoDbTableMock).putItem(cardMock);
        verify(cacheEvictionService).evictCacheEntry(any(BankAccountIdCacheKey.class));
    }

    @Test
    public void shouldThrowResourceNotFoundException_whenCardIdDoesNotExist() {
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(null);
        var nonExistentCardId = UUID.randomUUID();

        var exception = assertThrowsExactly(ResourceNotFoundException.class, () ->
                activateCardService.activateCard(nonExistentCardId)
        );

        assertAll(
                () -> assertThat(exception.getMessage()).isEqualTo("card.notFound"),
                () -> assertThat(exception.getRejectedIdentifier()).isEqualTo(nonExistentCardId)
        );

        verify(dynamoDbTableMock, never()).putItem(any(Card.class));
        verify(cacheEvictionService, never()).evictCacheEntry(any(BankAccountIdCacheKey.class));
    }
}

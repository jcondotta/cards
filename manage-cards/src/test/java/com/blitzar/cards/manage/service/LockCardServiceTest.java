package com.blitzar.cards.manage.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import com.blitzar.cards.exception.ResourceNotFoundException;
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
    private Card cardMock;

    private static final UUID CARD_ID = UUID.fromString("0d0c3534-1887-4a8d-9fe0-f25870813207");

    @BeforeEach
    public void beforeEach(){
        lockCardService = new LockCardService(dynamoDbTableMock);
    }

    @Test
    public void givenExistentCardId_whenLockCard_thenUpdateCardStatus(){
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(cardMock);

        lockCardService.lockCard(CARD_ID);

        verify(cardMock).setCardStatus(CardStatus.LOCKED);
        verify(dynamoDbTableMock).putItem(cardMock);
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
    }
}
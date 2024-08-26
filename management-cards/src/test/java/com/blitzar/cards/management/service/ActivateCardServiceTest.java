package com.blitzar.cards.management.service;

import com.blitzar.cards.domain.Card;
import com.blitzar.cards.domain.CardStatus;
import com.blitzar.cards.exception.ResourceNotFoundException;
import com.blitzar.cards.management.service.ActivateCardService;
import org.junit.jupiter.api.BeforeEach;
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
    private Card cardMock;

    private String cardId = "0d0c3534-1887-4a8d-9fe0-f25870813207";

    @BeforeEach
    public void beforeEach(){ }

    @Test
    public void givenExistentCardId_whenActivateCard_thenUpdateCardStatus(){
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(cardMock);

        activateCardService.activateCard(cardId);

        verify(cardMock).setCardStatus(CardStatus.ACTIVE);
        verify(dynamoDbTableMock).putItem(cardMock);
    }

    @Test
    public void givenNonExistentCardId_whenActivateCard_thenReturnNotFound(){
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(null);

        var nonExistentCardId = UUID.randomUUID().toString();
        var exception = assertThrowsExactly(ResourceNotFoundException.class, () -> activateCardService.activateCard(nonExistentCardId));

        assertAll(
                () -> assertThat(exception.getMessage()).isEqualTo("card.notFound"),
                () -> assertThat(exception.getRejectedIdentifier()).isEqualTo(nonExistentCardId)
        );

        verify(dynamoDbTableMock, never()).deleteItem(any(Card.class));
    }
}
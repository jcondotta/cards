package com.jcondotta.cards.management.service;

import com.jcondotta.cards.core.domain.Card;
import com.jcondotta.cards.core.domain.CardStatus;
import com.jcondotta.cards.core.exception.ResourceNotFoundException;
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

    private static final UUID CARD_ID = UUID.fromString("0d0c3534-1887-4a8d-9fe0-f25870813207");

    @Test
    public void shouldActivateCard_whenCardIdExists() {
        when(dynamoDbTableMock.getItem(any(Key.class))).thenReturn(cardMock);

        activateCardService.activateCard(CARD_ID);

        verify(cardMock).setCardStatus(CardStatus.ACTIVE);
        verify(dynamoDbTableMock).putItem(cardMock);
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
    }
}

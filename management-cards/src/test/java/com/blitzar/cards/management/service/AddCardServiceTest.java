package com.blitzar.cards.management.service;

import com.blitzar.cards.management.service.AddCardService;
import com.blitzar.cards.management.web.CardApplicationEventProducer;
import com.blitzar.cards.web.events.request.AddCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddCardServiceTest {

    @InjectMocks
    private AddCardService addCardService;

    @Mock
    private CardApplicationEventProducer eventProducer;

    private String bankAccountId = "998372";
    private String cardholderName = "Jefferson Condotta";

    @BeforeEach
    public void beforeEach(){ }

    @Test
    public void givenValidRequest_whenAddCard_thenSendSQSMessage(){
        var addCardRequest = new AddCardRequest(bankAccountId, cardholderName);

        addCardService.addCard(addCardRequest);

        verify(eventProducer).send(addCardRequest);
    }
}
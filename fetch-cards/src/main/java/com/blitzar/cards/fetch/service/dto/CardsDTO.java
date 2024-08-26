package com.blitzar.cards.fetch.service.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Serdeable
public record CardsDTO(Collection<CardDTO> cards) {

    @Override
    public Collection<CardDTO> cards() {
        return Objects.nonNull(cards) ? cards : new ArrayList<>();
    }
}
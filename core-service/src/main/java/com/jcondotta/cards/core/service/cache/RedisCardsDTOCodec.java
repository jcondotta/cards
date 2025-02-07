package com.jcondotta.cards.core.service.cache;

import com.jcondotta.cards.core.exception.CardDeserializationException;
import com.jcondotta.cards.core.exception.CardSerializationException;
import com.jcondotta.cards.core.service.dto.CardsDTO;
import io.lettuce.core.codec.RedisCodec;
import io.micronaut.json.JsonMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisCardsDTOCodec implements RedisCodec<String, CardsDTO> {

    private final JsonMapper jsonMapper;

    public RedisCardsDTOCodec(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        return StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }

    @Override
    public CardsDTO decodeValue(ByteBuffer byteBuffer) {
        byte[] array = new byte[byteBuffer.remaining()];
        byteBuffer.get(array);
        try {
            return jsonMapper.readValue(array, CardsDTO.class);
        }
        catch (IOException e) {
            throw new CardDeserializationException("Failed to deserialize CardsDTO", e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(CardsDTO cardsDTO) {
        try {
            byte[] bytes = jsonMapper.writeValueAsBytes(cardsDTO);
            return ByteBuffer.wrap(bytes);
        }
        catch (IOException e) {
            throw new CardSerializationException("Failed to serialize CardsDTO", e);
        }
    }
}

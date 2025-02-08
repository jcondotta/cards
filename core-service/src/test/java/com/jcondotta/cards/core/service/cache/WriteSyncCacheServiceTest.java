package com.jcondotta.cards.core.service.cache;

import com.jcondotta.cards.core.service.dto.CardDTO;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriteSyncCacheServiceTest {

//    private static final UUID CARD_ID = UUID.fromString("d17fa947-3044-44e0-8f95-641a170908e6");
//    private static final Long DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS = 3600L;
//
//    private WriteSyncCacheService writeSyncCacheService;
//
//    @Mock
//    private RedisCommands<String, CardDTO> redisCommands;
//
//    @Mock
//    private CardDTO cardDTO;
//
//    @BeforeEach
//    void beforeEach() {
//        writeSyncCacheService = new WriteSyncCacheService(redisCommands, DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS);
//    }
//
//    @Test
//    void shouldSetCacheEntry_whenCardIdIsValid() {
//        var cardsCacheKey = new CardsCacheKey(CARD_ID);
//        writeSyncCacheService.setCacheEntry(cardsCacheKey, cardDTO);
//
//        verify(redisCommands).setex(eq(cardsCacheKey.getKey()), eq(DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS), eq(cardDTO));
//        verifyNoMoreInteractions(redisCommands);
//    }
//
//    @Test
//    void shouldThrowNullPointerException_whenCacheKeyHasNullCardId() {
//        var exception = assertThrowsExactly(NullPointerException.class, () -> new CardsCacheKey(null));
//
//        assertThat(exception)
//                .satisfies(violation -> assertThat(violation.getMessage())
//                        .isEqualTo("cache.cards.cardId.notNull"));
//
//        verifyNoInteractions(redisCommands);
//    }
//
//    @Test
//    void shouldThrowNullPointerException_whenCardDTOIsNull() {
//        var cardsCacheKey = new CardsCacheKey(CARD_ID);
//
//        var exception = assertThrows(NullPointerException.class, () -> writeSyncCacheService.setCacheEntry(cardsCacheKey, null));
//
//        assertThat(exception)
//                .satisfies(violation -> assertThat(violation.getMessage())
//                        .isEqualTo("cache.cards.cacheValue.notNull"));
//
//        verifyNoInteractions(redisCommands);
//    }
}
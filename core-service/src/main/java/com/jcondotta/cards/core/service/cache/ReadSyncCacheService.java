package com.jcondotta.cards.core.service.cache;

import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Singleton
public class ReadSyncCacheService<V> {

    private final RedisCommands<String, V> redisCommands;

    @Inject
    public ReadSyncCacheService(RedisCommands<String, V> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public Optional<V> getCacheEntryValue(@NotNull CacheKey cacheKey) {
        return Optional.ofNullable(redisCommands.get(cacheKey.getKey()));
    }
}

package com.jcondotta.cards.core.service.cache;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Singleton
public class WriteAsyncCacheService<V> {

    private final RedisAsyncCommands<String, V> redisAsyncCommands;
    private final Long timeToLiveInSeconds;

    @Inject
    public WriteAsyncCacheService(RedisAsyncCommands<String, V> redisAsyncCommands, @Value("${redis.cache.cards.ttl-in-seconds}") Long timeToLiveInSeconds) {
        this.redisAsyncCommands = redisAsyncCommands;
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }

    public void setCacheEntry(@NotNull CacheKey cacheKey, @NotNull V cacheValue) {
        Objects.requireNonNull(cacheKey, "Cache key cannot be null");
        Objects.requireNonNull(cacheValue, "Cache value cannot be null");

        redisAsyncCommands.setex(cacheKey.getKey(), timeToLiveInSeconds, cacheValue);
    }
}

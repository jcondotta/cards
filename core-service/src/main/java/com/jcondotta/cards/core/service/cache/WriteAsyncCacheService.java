package com.jcondotta.cards.core.service.cache;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Singleton
public class WriteAsyncCacheService<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteAsyncCacheService.class);

    private final RedisAsyncCommands<String, V> redisAsyncCommands;
    private final Long timeToLiveInSeconds;

    @Inject
    public WriteAsyncCacheService(RedisAsyncCommands<String, V> redisAsyncCommands,
                                  @Value("${redis.cache.cards.ttl-in-seconds}") Long timeToLiveInSeconds) {
        this.redisAsyncCommands = redisAsyncCommands;
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }

    public void setCacheEntry(@NotNull CacheKey cacheKey, @NotNull V cacheValue) {
        Objects.requireNonNull(cacheKey, "Cache key cannot be null");
        Objects.requireNonNull(cacheValue, "Cache value cannot be null");

        redisAsyncCommands.setex(cacheKey.getKey(), timeToLiveInSeconds, cacheValue)
                .whenComplete((result, throwable) -> {

                    if (throwable != null) {
                        LOGGER.error("Failed to write cache entry",
                                StructuredArguments.keyValue("cacheKey", cacheKey.getKey()),
                                StructuredArguments.keyValue("error", throwable.getMessage()),
                                throwable
                        );
                    } else {
                        LOGGER.info("Cache entry successfully written",
                                StructuredArguments.keyValue("cacheKey", cacheKey.getKey()),
                                StructuredArguments.keyValue("ttlSeconds", timeToLiveInSeconds)
                        );
                    }
                });
    }
}

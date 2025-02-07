package com.jcondotta.cards.core.service.cache;

import io.lettuce.core.api.async.RedisAsyncCommands;
import jakarta.inject.Singleton;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CacheEvictionService<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEvictionService.class);

    private final RedisAsyncCommands<String, V> redisCommands;

    public CacheEvictionService(RedisAsyncCommands<String, V> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public void evictCacheEntry(CacheKey cacheKey){
        LOGGER.info("Invalidating cache entry",
                StructuredArguments.keyValue("cacheKey", cacheKey.getKey())
        );

        redisCommands.del(cacheKey.getKey());

        LOGGER.info("Cache entry removed",
                StructuredArguments.keyValue("cacheKey", cacheKey.getKey())
        );
    }
}

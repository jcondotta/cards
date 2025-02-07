package com.jcondotta.cards.core.service.cache;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class CardsCacheService<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardsCacheService.class);

    private final WriteAsyncCacheService<V> writeAsyncCacheService;
    private final ReadSyncCacheService<V> readSyncCacheService;

    @Inject
    public CardsCacheService(WriteAsyncCacheService<V> writeAsyncCacheService, ReadSyncCacheService<V> readSyncCacheService) {
        this.writeAsyncCacheService = writeAsyncCacheService;
        this.readSyncCacheService = readSyncCacheService;
    }

    public void setCacheEntry(CacheKey cacheKey, V cacheValue) {
        LOGGER.info("Storing card in cache",
                StructuredArguments.keyValue("cacheKey", cacheKey.getKey())
        );

        writeAsyncCacheService.setCacheEntry(cacheKey, cacheValue);
    }

    public Optional<V> getCacheEntryValue(CacheKey cacheKey) {
        Optional<V> cacheEntryValue = readSyncCacheService.getCacheEntryValue(cacheKey);

        cacheEntryValue.ifPresentOrElse(
                cardDTO -> LOGGER.info("Cache hit",
                        StructuredArguments.keyValue("cacheKey", cacheKey.getKey())
                ),
                () -> LOGGER.info("Cache miss",
                        StructuredArguments.keyValue("cacheKey", cacheKey.getKey())
                )
        );

        return cacheEntryValue;
    }
}

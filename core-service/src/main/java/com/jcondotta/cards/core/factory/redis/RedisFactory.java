package com.jcondotta.cards.core.factory.redis;

import com.jcondotta.cards.core.service.cache.RedisCardsDTOCodec;
import com.jcondotta.cards.core.service.dto.CardsDTO;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class RedisFactory {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RedisFactory.class);

    @Singleton
    RedisCardsDTOCodec redisCardsDTOCodec(JsonMapper jsonMapper){
        return new RedisCardsDTOCodec(jsonMapper);
    }

    @Singleton
    @Replaces(RedisURI.class)
    RedisURI redisURI(@Value("${redis.uri.host}") String redisURIHost, @Value("${redis.uri.port}") int redisURIPort) {
//        LOGGER.info("Building Redis URI with host: {}, port: {}, SSL enabled: {}", redisURIHost, redisURIPort, ssl);

        return RedisURI.builder()
                .withHost(redisURIHost)
                .withPort(redisURIPort)
                .withSsl(false)
                .build();
    }

    @Singleton
    @Replaces(RedisClient.class)
    public RedisClient redisClient(RedisURI redisURI) {
//        LOGGER.info("Creating Redis client with URI: {}", redisURI);
        return RedisClient.create(redisURI);
    }

    @Singleton
    @Replaces(StatefulRedisConnection.class)
    public StatefulRedisConnection<String, CardsDTO> redisCardsDTOConnection(RedisClient redisClient, RedisCardsDTOCodec codec) {
        return redisClient.connect(codec);
    }

    @Singleton
    @Replaces(RedisCommands.class)
    public RedisCommands<String, CardsDTO> redisCommands(StatefulRedisConnection<String, CardsDTO> statefulRedisConnection) {
        return statefulRedisConnection.sync();
    }

    @Singleton
    @Replaces(RedisAsyncCommands.class)
    public RedisAsyncCommands<String, CardsDTO> redisAsyncCommands(StatefulRedisConnection<String, CardsDTO> statefulRedisConnection) {
        return statefulRedisConnection.async();
    }
}

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisFactory.class);

    @Singleton
    RedisCardsDTOCodec redisCardsDTOCodec(JsonMapper jsonMapper){
        return new RedisCardsDTOCodec(jsonMapper);
    }

    @Singleton
    @Replaces(RedisURI.class)
    RedisURI redisURI(@Value("${redis.uri.host}") String redisURIHost,
                      @Value("${redis.uri.port}") int redisURIPort,
                      @Value("${redis.ssl}") boolean ssl) {
        LOGGER.info("Building Redis URI with host: {}, port: {}, SSL enabled: {}", redisURIHost, redisURIPort, ssl);

        return RedisURI.builder()
                .withHost(redisURIHost)
                .withPort(redisURIPort)
                .withSsl(ssl)
                .build();
    }

    @Singleton
    @Replaces(RedisClient.class)
    public RedisClient redisClient(RedisURI redisURI) {
        LOGGER.info("Creating Redis client with URI: {}", redisURI);
        return RedisClient.create(redisURI);
    }

    @Singleton
    @Replaces(StatefulRedisConnection.class)
    public StatefulRedisConnection<String, CardsDTO> redisCardsDTOConnection(RedisClient redisClient, RedisCardsDTOCodec codec) {
        var redis = redisClient.connect();
        RedisCommands<String, String> sync = redis.sync();
        sync.set("Jefferson", "Condotta");
        System.out.println(sync.get("Jefferson"));
        return redisClient.connect(codec);
    }

    @Singleton
    public RedisCommands<String, CardsDTO> redisCommands(StatefulRedisConnection<String, CardsDTO> statefulRedisConnection) {
        System.out.println("RedisCommand");
        return statefulRedisConnection.sync();
    }

    @Singleton
    @Replaces(RedisAsyncCommands.class)
    public RedisAsyncCommands<String, CardsDTO> redisAsyncCommands(StatefulRedisConnection<String, CardsDTO> statefulRedisConnection) {
        System.out.println("RedisAsyncCommand");
        return statefulRedisConnection.async();
    }


//
//    @Singleton
//    @Replaces(RedisClient.class)
//    public RedisClient redisClient(RedisURI redisURI) {
//        LOGGER.info("Creating Redis client with URI: {}", redisURI);
//        return RedisClient.create(redisURI);
//    }

//    @Singleton
//    RedisCommands<String, RecipientsDTO> redisCommands(RedisClient redisClient,
//                                                       RedisRecipientsDTOCodec redisRecipientsDTOCodec) {
//        LOGGER.info("Establishing connection to Redis with custom codec");
//
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            LOGGER.info("Connection established successfully. Adding a key-value pair in Redis.");
//            connection.sync().set("Jefferson", "Condotta");
//
//            String jeffersonValue = connection.sync().get("Jefferson");
//            LOGGER.info("Retrieved value for 'Jefferson': {}", jeffersonValue);
//        } catch (Exception e) {
//            LOGGER.error("Error establishing initial Redis connection: {}", e.getMessage(), e);
//        }
//
//        StatefulRedisConnection<String, RecipientsDTO> connection = null;
//        try {
//            connection = redisClient.connect(redisRecipientsDTOCodec);
//            LOGGER.info("Connected with custom codec. Setting 'Jefferson1' with RecipientsDTO data.");
//
//            connection.sync().set("Jefferson1", new RecipientsDTO(List.of(
//                    new RecipientDTO(UUID.randomUUID(), "Jefferson Condotta", "IT49 W030 0203 2801 1452 4628 85 7", LocalDateTime.now())
//            ), 1, null));
//
//            RecipientsDTO recipients = connection.sync().get("Jefferson1");
//            if (recipients != null) {
//                recipients.recipients().forEach(recipientDTO -> LOGGER.info("Retrieved RecipientDTO: {}", recipientDTO));
//            }
//        } catch (Exception e) {
//            LOGGER.error("Error during Redis command operations: {}", e.getMessage(), e);
//        }
//
//        return connection != null ? connection.sync() : null;
//    }
}

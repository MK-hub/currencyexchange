package com.example.currencyexchange.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String ACCOUNT_CACHE = "ACCOUNT :";
    public static final String EXCHANGE_CACHE = "EXCHANGE :";
    public static final String CUSTOM_GENERATOR = "customKeyGenerator";

    @Value("${cache.expiration.account}")
    private long accountCacheTtl;

    @Value("${cache.expiration.exchange}")
    private long exchangeCacheTtl;

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(cacheObjectMapper())));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder ->
                builder.withCacheConfiguration(ACCOUNT_CACHE, redisCacheConfiguration()
                                .entryTtl(Duration.ofMinutes(accountCacheTtl))
                                .computePrefixWith(cacheName -> ACCOUNT_CACHE))
                        .withCacheConfiguration(EXCHANGE_CACHE, redisCacheConfiguration()
                                .entryTtl(Duration.ofHours(exchangeCacheTtl))
                                .computePrefixWith(cacheName -> EXCHANGE_CACHE));
    }

    private ObjectMapper cacheObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        return objectMapper;
    }

    @Bean(CUSTOM_GENERATOR)
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}

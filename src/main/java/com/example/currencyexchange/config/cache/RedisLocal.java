package com.example.currencyexchange.config.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@EnableCaching
@Profile("!test")
public class RedisLocal {
    private static final int PORT = 6370;
    private RedisServer redisServer;

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer = new RedisServer(PORT);
        redisServer.start();
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() { return new LettuceConnectionFactory("localhost", PORT);}

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}

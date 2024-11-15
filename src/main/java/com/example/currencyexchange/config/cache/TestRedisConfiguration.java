package com.example.currencyexchange.config.cache;

import com.example.currencyexchange.errorhandling.RedisException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
@Profile("test")
public class TestRedisConfiguration {
    private static RedisServer redisServer;
    private static final int PORT = 6370;

    static {
        try {
            redisServer = new RedisServer(PORT);
            redisServer.start();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("bind")) {
                log.info("Redis server already running on port {}", PORT);
            } else {
                throw new RedisException(e);
            }
        }
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", PORT);
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }
}

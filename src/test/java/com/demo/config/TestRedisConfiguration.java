package com.demo.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(RedisConfig redisConfig) throws IOException {
        this.redisServer = new RedisServer(redisConfig.getPort());
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }
}

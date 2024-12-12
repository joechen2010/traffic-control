package com.demo.service.impl;

import com.demo.config.Config;
import com.demo.controller.ApiController;
import com.demo.service.RateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component("redisRateLimiterService")
public class RedisRateLimiterService implements RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiterService.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Config config;


    public boolean tryAcquire(String userId, String apiPath) {
        String key = getRateLimiterKey(userId, apiPath);
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        logger.info("Rate limiter key: {}, current count: {}", key, currentCount);
        return currentCount <= config.getPermitsPerMinute();
    }

}

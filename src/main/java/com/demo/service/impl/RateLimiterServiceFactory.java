package com.demo.service.impl;

import com.demo.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterServiceFactory {

    @Value("${rate.limiter.type:memory}")
    private String rateLimiterType;

    @Autowired
    @Qualifier("memoryRateLimiterService")
    private RateLimiterService memoryRateLimiterService;

    @Autowired
    @Qualifier("redisRateLimiterService")
    private RateLimiterService redisRateLimiterService;

    @Autowired
    @Qualifier("redissonRateLimiterService")
    private RateLimiterService redissonRateLimiterService;

    public RateLimiterService getRateLimiterService() {
        if (rateLimiterType.equals("memory")) {
            return memoryRateLimiterService;
        } else if (rateLimiterType.equals("redis")) {
            return redisRateLimiterService;
        } else if (rateLimiterType.equals("redisson")) {
            return redissonRateLimiterService;
        } else {
            throw new IllegalArgumentException("Invalid rate limiter type: " + rateLimiterType);
        }
    }
}

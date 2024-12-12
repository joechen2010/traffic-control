package com.demo.service.impl;

import com.demo.config.Config;
import com.demo.service.RateLimiterService;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("redissonRateLimiterService")
public class RedissonRateLimiterService implements RateLimiterService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private Config config;

    @Override
    public boolean tryAcquire(String userId, String apiPath) {
        String key = getRateLimiterKey(userId, apiPath);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.PER_CLIENT, config.getPermitsPerMinute(), 1, RateIntervalUnit.MINUTES);
        return rateLimiter.tryAcquire(1, 500, TimeUnit.MILLISECONDS);
    }
}

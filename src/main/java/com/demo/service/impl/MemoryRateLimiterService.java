package com.demo.service.impl;

import com.demo.config.Config;
import com.demo.service.RateLimiterService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component("memoryRateLimiterService")
public class MemoryRateLimiterService implements RateLimiterService {

    @Autowired
    private Config config;

    private Cache<String, RateLimiter> rateLimiterRegistry  = CacheBuilder.newBuilder()
            .maximumSize(20000L)
            .expireAfterAccess(1L, TimeUnit.MINUTES)
            .build();

    public boolean tryAcquire(String userId, String apiPath) {
        RateLimiter rateLimiter = null;
        try {
            String key = getRateLimiterKey(userId, apiPath);
            rateLimiter = rateLimiterRegistry.get(key, () ->
                    RateLimiter.create(config.getPermitsPerMinute() / 60.0));
        } catch (ExecutionException e) {

        }
        return rateLimiter != null && rateLimiter.tryAcquire();
    }
}

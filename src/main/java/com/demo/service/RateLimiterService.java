package com.demo.service;

public interface RateLimiterService {

    boolean tryAcquire(String userId, String apiPath);

    default String getRateLimiterKey(String userId, String apiPath) {
        // user level, all APIs share the same rate limiter
        // api level, all APIs have their own rate limiter, shoud be userId + apiPath
        return userId;
    }
}

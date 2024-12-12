package com.demo.controller;

import com.demo.service.MetricService;
import com.demo.service.impl.RateLimiterServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private MetricService metricService;

    @Autowired
    private RateLimiterServiceFactory rateLimiterServiceFactory;

    @GetMapping("/1")
    public ResponseEntity<Void> api1(@RequestParam String userId) {
        return handleRequest(userId, "api1");
    }

    @PostMapping("/2")
    public ResponseEntity<Void> api2(@RequestParam String userId) {
        return handleRequest(userId, "api2");
    }

    @PutMapping("/3")
    public ResponseEntity<Void> api3(@RequestParam String userId) {
        return handleRequest(userId, "api3");
    }

    protected ResponseEntity<Void> handleRequest(String userId, String apiName) {
        long startTime = System.currentTimeMillis();
        HttpStatus status;
        if (rateLimiterServiceFactory.getRateLimiterService().tryAcquire(userId, apiName)) {
            // process request
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.TOO_MANY_REQUESTS;
        }
        metricService.recodeTimer(apiName, status.value(), System.currentTimeMillis() - startTime);
        logger.debug("API {} called by user {} with status {}", apiName, userId, status.value());
        return ResponseEntity.status(status).build();
    }
}

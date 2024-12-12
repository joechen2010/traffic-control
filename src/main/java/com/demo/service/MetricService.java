package com.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class MetricService {

    @Autowired
    private MeterRegistry meterRegistry;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    public void recodeTimer(String apiName, int status, long latency) {
        executor.submit(() -> {
            String metricName = apiName + "." + status;
            Timer timer = meterRegistry.find(metricName).timer();
            if (timer == null) {
                timer = Timer.builder(metricName)
                        .description("Request time")
                        .tag("apiName",apiName)
                        .publishPercentileHistogram()
                        .register(meterRegistry);
            }
            timer.record(latency, TimeUnit.MILLISECONDS);
        });
    }
}

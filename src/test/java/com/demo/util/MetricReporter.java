package com.demo.util;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class MetricReporter {

    private MeterRegistry meterRegistry;

    public static MetricReporter of(MeterRegistry meterRegistry) {
        MetricReporter metricReporter = new MetricReporter();
        metricReporter.meterRegistry = meterRegistry;
        return metricReporter;
    }

    public  void start(){
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this::report, 0, 10, TimeUnit.SECONDS);
    }

    public void report(){
        System.out.println("============== Metric report =============");
        for(int i = 1; i <= 3;i++){
            String metricName_200 = "api" + i + ".200";
            String metricName_429 = "api" + i + ".429";
            Timer timer_200 = meterRegistry.find(metricName_200).timer();
            Timer timer_429 = meterRegistry.find(metricName_429).timer();
            if(timer_200 != null){
                System.out.println(metricName_200 + getMetricInfo(timer_200));
            }
            if(timer_429 != null){
                System.out.println(metricName_429 + getMetricInfo(timer_429));
            }
        }
    }

    public static String getMetricInfo(Timer timer){
        if(timer == null){
            return null;
        }
        long count = timer.count();
        double totalTimeInSeconds = timer.totalTime(TimeUnit.SECONDS);
        double time = totalTimeInSeconds < 1 ? 1 : totalTimeInSeconds;
        double tps = totalTimeInSeconds > 0 ? count / time : 0;
        HistogramSnapshot snapshot = timer.takeSnapshot();
        return String.format(
                " TPS: %.2f/s, Total count: %d, Total time: %.2f ms, Max time: %.2f ms, Mean time: %.2f ms",
                tps,
                snapshot.count(),
                snapshot.total(TimeUnit.MILLISECONDS),
                snapshot.max(TimeUnit.MILLISECONDS),
                snapshot.mean(TimeUnit.MILLISECONDS)
        );
    }
}

package com.demo.bench;

import com.demo.TrafficControllApplication;
import com.demo.controller.ApiController;
import com.demo.util.MetricReporter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Fork(value = 1)
@Threads(1)
public class ApiBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(ApiBenchmark.class);

    @Autowired
    private ApiController controller;
    private MeterRegistry meterRegistry;

    private AnnotationConfigApplicationContext context;

    private Random random = new Random();


    @Setup(Level.Trial)
    public void setUp() {
        context = new AnnotationConfigApplicationContext(TrafficControllApplication.class);
        controller = context.getBean(ApiController.class);
        meterRegistry = context.getBean(MeterRegistry.class);
        MetricReporter.of(meterRegistry).start();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        context.close();
    }

    @Test
    void benchMarkTest() throws Exception {
        Options opt = new OptionsBuilder()
                .include(ApiBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 120, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 500)
    @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
    public void bench(Blackhole blackhole) {
        int api = random.nextInt(3) + 1;
        String userId = "String.valueOf(random.nextInt(4) + 1)";
        switch (api){
            case 1:
                controller.api1(userId);
                break;
            case 2:
                controller.api2(userId);
                break;
            case 3:
                controller.api3(userId);
                break;
        }
        blackhole.consume(controller);
    }
}

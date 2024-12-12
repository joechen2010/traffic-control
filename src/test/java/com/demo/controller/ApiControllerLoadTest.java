package com.demo.controller;


import com.demo.config.TestRedisConfiguration;
import com.demo.util.Benchmarker;
import com.demo.util.MetricReporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.demo.util.MetricReporter.getMetricInfo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
class ApiControllerLoadTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiControllerLoadTest.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    public void setUp() {
        meterRegistry.clear();
    }

    @Test
    void shouldRejectRequestsOverThreshold() throws Exception {
        Benchmarker benchmarker = Benchmarker.of(mockMvc, 4, 500, 60);
        benchmarker.start();

        Timer app1_success_timer = meterRegistry.find("api1.200").timer();
        Timer app1_fail_timer = meterRegistry.find("api1.429").timer();
        Timer app2_success_timer = meterRegistry.find("api2.200").timer();
        Timer app2_fail_timer = meterRegistry.find("api2.429").timer();
        Timer app3_success_timer = meterRegistry.find("api3.200").timer();
        Timer app3_fail_timer = meterRegistry.find("api3.429").timer();
        Assertions.assertNotNull(app1_success_timer);
        Assertions.assertNotNull(app1_fail_timer);
        Assertions.assertNotNull(app2_success_timer);
        Assertions.assertNotNull(app2_fail_timer);
        Assertions.assertNotNull(app3_success_timer);
        Assertions.assertNotNull(app3_fail_timer);

        long totalTeceivedRequests = app1_success_timer.count()
                + app1_fail_timer.count()
                + app2_success_timer.count()
                + app2_fail_timer.count()
                + app3_success_timer.count()
                + app3_fail_timer.count();
        int totalSentRequests = benchmarker.getTotalRequestCount();
        Assertions.assertEquals(totalSentRequests, totalTeceivedRequests);
        System.out.println("Total Sent Requests: " + totalSentRequests + ",Total Received Requests: " + totalTeceivedRequests);
        MetricReporter.of(meterRegistry).report();
    }


}

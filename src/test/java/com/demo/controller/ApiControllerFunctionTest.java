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
class ApiControllerFunctionTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiControllerFunctionTest.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    public void setUp() {
        meterRegistry.clear();
    }

    @Test
    void shouldAllowForApi1() throws Exception {
        mockMvc.perform(get("/api/1")
                .param("userId", "user1"))
                .andExpect(status().isOk())
                .andReturn();
        Timer timer = meterRegistry.find("api1.200").timer();
        Assertions.assertNotNull(timer);
        logger.info(getMetricInfo(timer));

        Timer timer_fail = meterRegistry.find("api1.429").timer();
        Assertions.assertNull(timer_fail);
    }

}

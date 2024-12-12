package com.demo.util;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Benchmarker {

    private int userCount = 4;
    private int requestCountPerUser = 500;
    private int looperCount = 60;
    private ExecutorService userExecutor;
    private ExecutorService requestExecutor;
    private Random random = new Random();
    private MockMvc mockMvc;
    private CountDownLatch cowdownLatch = new CountDownLatch(userCount * requestCountPerUser * looperCount);

    public static Benchmarker of(MockMvc mockMvc, int userCount, int requestCountPerUser, int looperCount) {
        Benchmarker benchmarker = new Benchmarker();
        benchmarker.userCount = userCount;
        benchmarker.requestCountPerUser = requestCountPerUser;
        benchmarker.looperCount = looperCount;
        benchmarker.userExecutor = Executors.newFixedThreadPool(userCount);
        benchmarker.requestExecutor = Executors.newFixedThreadPool(requestCountPerUser);
        benchmarker.mockMvc = mockMvc;
        return benchmarker;
    }

    public int getTotalRequestCount() {
        return userCount * requestCountPerUser * looperCount;
    }

    public void start() {
        for (int i = 0; i < userCount; i++) {
            final int userIndex = i;
            userExecutor.execute(() -> {
                for (int j = 0; j < looperCount; j++) {
                    executeSingleUserRequest(userIndex);
                }
            });
        }
        try {
            cowdownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeSingleUserRequest(int userIndex) {
        for (int j = 0; j < requestCountPerUser; j++) {
            requestExecutor.execute(() -> {
                sendRequest(userIndex);
            });
        }
    }

    public void sendRequest( int userIndex){
        String api = "/api/" + (random.nextInt(3) + 1);
        String userId = "user" + userIndex;
        MockHttpServletRequestBuilder requestBuilder;
        switch (api) {
            case "/api/1":
                requestBuilder = MockMvcRequestBuilders.get(api).param("userId", userId);
                break;
            case "/api/2":
                requestBuilder = MockMvcRequestBuilders.post(api).param("userId", userId);
                break;
            case "/api/3":
                requestBuilder = MockMvcRequestBuilders.put(api).param("userId", userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid API URL");
        }
        try {
            mockMvc.perform(requestBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cowdownLatch.countDown();
        }
    }
}

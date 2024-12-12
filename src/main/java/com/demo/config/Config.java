package com.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate.limiter")
public class Config {

    private int permitsPerMinute = 10000;

    public int getPermitsPerMinute() {
        return permitsPerMinute;
    }

    public void setPermitsPerMinute(int permitsPerMinute) {
        this.permitsPerMinute = permitsPerMinute;
    }
}

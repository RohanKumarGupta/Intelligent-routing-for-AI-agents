package com.agentconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private int postsPerHour = 100;
    private int commentsPerHour = 200;
    private int apiRequestsPerMinute = 60;
}

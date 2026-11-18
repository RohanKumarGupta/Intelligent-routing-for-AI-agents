package com.agentconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String provider = "mock";
    private int embeddingDimensions = 768;
    private String modelName = "gpt-4";
}

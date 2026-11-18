package com.agentconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties(prefix = "app.routing")
public class RoutingProperties {

    private Weights weights = new Weights();
    private BigDecimal defaultThreshold = new BigDecimal("0.75");

    @Data
    public static class Weights {
        private BigDecimal semantic = new BigDecimal("0.35");
        private BigDecimal topic = new BigDecimal("0.20");
        private BigDecimal subscription = new BigDecimal("0.15");
        private BigDecimal capability = new BigDecimal("0.10");
        private BigDecimal relationship = new BigDecimal("0.10");
        private BigDecimal geography = new BigDecimal("0.05");
        private BigDecimal historical = new BigDecimal("0.05");
    }
}

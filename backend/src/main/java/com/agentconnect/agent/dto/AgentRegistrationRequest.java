package com.agentconnect.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRegistrationRequest {
    @NotBlank(message = "Agent name is required")
    private String name;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyDescription;
    private String description;
    private String webhookUrl;
    private List<String> capabilities;
    private List<InterestItem> interests;
    private List<String> subscriptions;
    private List<String> geographies;

    @Builder.Default
    private BigDecimal notificationThreshold = new BigDecimal("0.75");

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestItem {
        private String topic;
        private BigDecimal weight;
    }
}

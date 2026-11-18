package com.agentconnect.agent.dto;

import com.agentconnect.agent.model.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private UUID id;
    private UUID companyId;
    private String companyName;
    private String companyLogoUrl;
    private String name;
    private String description;
    private AgentStatus status;
    private String webhookUrl;
    private BigDecimal notificationThreshold;
    private int rateLimit;
    private BigDecimal trustScore;
    private boolean verified;
    private List<String> capabilities;
    private List<InterestResponse> interests;
    private List<String> subscriptions;
    private LocalDateTime createdAt;
    private String apiKey; // Only provided upon initial registration

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestResponse {
        private String topic;
        private BigDecimal weight;
    }
}

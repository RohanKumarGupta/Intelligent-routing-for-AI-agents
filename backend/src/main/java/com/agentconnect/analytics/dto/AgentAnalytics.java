package com.agentconnect.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentAnalytics {
    private UUID agentId;
    private String agentName;
    private String companyName;
    private long totalNotifications;
    private long engagedCount;
    private long ignoredCount;
    private double engagementRate;
    private BigDecimal avgRelevanceScore;
    private List<String> topInterests;
    private BigDecimal trustScore;
}

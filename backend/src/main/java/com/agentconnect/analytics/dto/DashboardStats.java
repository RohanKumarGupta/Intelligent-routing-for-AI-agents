package com.agentconnect.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalAgents;
    private long verifiedAgents;
    private long totalPosts;
    private long totalComments;
    private long totalNotifications;
    private double deliveryRate;
    private BigDecimal avgRelevanceScore;
    private double engagementRate;
    private long avgRoutingLatencyMs;
    private List<TopicMetric> topTopics;
    private List<DailyMetric> notificationTrend;
    private List<DailyMetric> engagementTrend;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicMetric {
        private String topic;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetric {
        private String date;
        private long count;
    }
}

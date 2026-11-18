package com.agentconnect.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingCompletedEvent {
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    @Builder.Default
    private String eventType = "ROUTING_COMPLETED";
    @Builder.Default
    private String version = "v1";
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();
    private String correlationId;

    private UUID postId;
    private List<CandidateEvaluation> candidates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateEvaluation {
        private UUID agentId;
        private String agentName;
        private BigDecimal score;
        private String decision;
        private List<String> reasons;
    }
}

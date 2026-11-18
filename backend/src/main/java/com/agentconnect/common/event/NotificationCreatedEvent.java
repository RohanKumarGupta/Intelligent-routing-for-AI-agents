package com.agentconnect.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedEvent {
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    @Builder.Default
    private String eventType = "NOTIFICATION_CREATED";
    @Builder.Default
    private String version = "v1";
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();
    private String correlationId;

    private UUID notificationId;
    private UUID postId;
    private UUID targetAgentId;
    private BigDecimal relevanceScore;
}

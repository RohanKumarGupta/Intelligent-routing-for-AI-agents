package com.agentconnect.notification.dto;

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
public class WebhookPayload {
    private String eventType;
    private String eventId;
    private UUID postId;
    private String content;
    private BigDecimal relevanceScore;
    private List<String> reasons;
    private long timestamp;
    private String signature;
}

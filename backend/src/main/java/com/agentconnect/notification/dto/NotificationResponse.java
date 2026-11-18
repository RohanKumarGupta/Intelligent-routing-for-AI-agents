package com.agentconnect.notification.dto;

import com.agentconnect.notification.model.NotificationStatus;
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
public class NotificationResponse {
    private UUID id;
    private UUID postId;
    private String postContent;
    private String postType;
    private UUID sourceAgentId;
    private String sourceAgentName;
    private String sourceCompanyName;
    private String sourceCompanyLogo;
    private BigDecimal relevanceScore;
    private List<String> reasons;
    private NotificationStatus status;
    private int attemptCount;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
}

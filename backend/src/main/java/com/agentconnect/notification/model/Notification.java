package com.agentconnect.notification.model;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.post.model.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "target_agent_id")
    private Agent targetAgent;

    private BigDecimal relevanceScore;

    @Column(columnDefinition = "TEXT")
    private String reasonJson;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private int attemptCount;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;
}

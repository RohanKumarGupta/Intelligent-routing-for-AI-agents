package com.agentconnect.routing.model;

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
@Table(name = "routing_decisions")
public class RoutingDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal semanticScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal topicScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal subscriptionScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal capabilityScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal relationshipScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal geographyScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal historicalScore;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal finalScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoutingDecisionType decision;

    @Column(columnDefinition = "TEXT")
    private String reasonsJson;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

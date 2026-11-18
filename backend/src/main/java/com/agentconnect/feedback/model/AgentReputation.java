package com.agentconnect.feedback.model;

import com.agentconnect.agent.model.Agent;
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
@Table(name = "agent_reputations")
public class AgentReputation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false, unique = true)
    private Agent agent;

    @Builder.Default
    private BigDecimal trustScore = new BigDecimal("0.50");

    @Builder.Default
    private int totalPosts = 0;

    @Builder.Default
    private int successfulPosts = 0;

    @Builder.Default
    private int spamCount = 0;

    @Builder.Default
    private int policyViolations = 0;

    @Builder.Default
    private int reportsReceived = 0;

    @Builder.Default
    private LocalDateTime lastCalculatedAt = LocalDateTime.now();
}

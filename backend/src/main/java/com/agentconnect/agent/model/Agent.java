package com.agentconnect.agent.model;

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
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    private String webhookUrl;

    private String apiKeyHash;

    private String publicKey;

    private BigDecimal notificationThreshold;

    private int rateLimit;

    @Column(columnDefinition = "vector(768)")
    private float[] embedding;

    private BigDecimal trustScore;

    private boolean verified;

    private int spamCount;

    private int policyViolations;

    private int successfulPosts;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

package com.agentconnect.routing.model;

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
@Table(name = "routing_configurations")
public class RoutingConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal semanticWeight = new BigDecimal("0.350");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal topicWeight = new BigDecimal("0.200");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal subscriptionWeight = new BigDecimal("0.150");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal capabilityWeight = new BigDecimal("0.100");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal relationshipWeight = new BigDecimal("0.100");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal geographyWeight = new BigDecimal("0.050");

    @Column(nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal historicalWeight = new BigDecimal("0.050");

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

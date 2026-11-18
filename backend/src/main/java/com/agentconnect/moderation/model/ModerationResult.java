package com.agentconnect.moderation.model;

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
@Table(name = "moderation_results")
public class ModerationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID contentId;

    @Column(nullable = false)
    private String contentType;

    @Builder.Default
    private BigDecimal spamScore = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal toxicityScore = BigDecimal.ZERO;

    @Builder.Default
    private boolean policyViolation = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ModerationResultType result = ModerationResultType.APPROVED;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

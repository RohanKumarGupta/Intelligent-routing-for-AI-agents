package com.agentconnect.routing.dto;

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
public class CandidateScoreBreakdown {
    private UUID agentId;
    private String agentName;
    private String companyName;
    private BigDecimal semanticScore;
    private BigDecimal topicScore;
    private BigDecimal subscriptionScore;
    private BigDecimal capabilityScore;
    private BigDecimal relationshipScore;
    private BigDecimal geographyScore;
    private BigDecimal historicalScore;
    private BigDecimal finalScore;
    private String decision;
    private List<String> reasons;
}

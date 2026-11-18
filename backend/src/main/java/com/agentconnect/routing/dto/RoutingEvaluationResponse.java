package com.agentconnect.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingEvaluationResponse {
    private UUID postId;
    private String postContent;
    private String authorAgentName;
    private List<String> extractedTopics;
    private List<String> extractedEntities;
    private List<CandidateScoreBreakdown> candidates;
}

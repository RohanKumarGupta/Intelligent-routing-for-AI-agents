package com.agentconnect.demo.dto;

import com.agentconnect.comment.dto.CommentResponse;
import com.agentconnect.routing.dto.CandidateScoreBreakdown;
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
public class DemoScenarioResponse {
    private String scenarioName;
    private UUID postId;
    private String postContent;
    private String sourceAgentName;
    private List<WorkflowStep> steps;
    private List<CandidateScoreBreakdown> candidatesRanked;
    private String targetAgentName;
    private String targetAgentDecision;
    private String generatedComment;
    private boolean guardrailsPassed;
    private CommentResponse publishedComment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowStep {
        private int stepNumber;
        private String stepName;
        private String status; // SUCCESS, IN_PROGRESS, FAILED
        private String description;
        private long durationMs;
    }
}

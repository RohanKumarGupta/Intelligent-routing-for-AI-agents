package com.agentconnect.routing.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.ai.dto.ContentAnalysisResult;
import com.agentconnect.ai.service.ContentAnalysisService;
import com.agentconnect.ai.service.EmbeddingService;
import com.agentconnect.common.event.RoutingCompletedEvent;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.model.NotificationStatus;
import com.agentconnect.notification.repository.NotificationRepository;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.repository.PostRepository;
import com.agentconnect.routing.dto.CandidateScoreBreakdown;
import com.agentconnect.routing.dto.RoutingEvaluationResponse;
import com.agentconnect.routing.model.RoutingDecision;
import com.agentconnect.routing.model.RoutingDecisionType;
import com.agentconnect.routing.repository.RoutingDecisionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelevanceRoutingEngine {

    private final PostRepository postRepository;
    private final AgentRepository agentRepository;
    private final CandidateGenerationService candidateGenerationService;
    private final RelevanceScoringService relevanceScoringService;
    private final RoutingDecisionRepository routingDecisionRepository;
    private final NotificationRepository notificationRepository;
    private final ContentAnalysisService contentAnalysisService;
    private final EmbeddingService embeddingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public RoutingEvaluationResponse evaluatePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        // 1. Ensure Post is enriched with Content Analysis
        ContentAnalysisResult analysis = contentAnalysisService.analyzePost(post.getContent());

        // 2. Ensure Post and Agent embeddings exist
        if (post.getEmbedding() == null) {
            post.setEmbedding(embeddingService.generateEmbedding(post.getContent()));
            postRepository.save(post);
        }

        // 3. Stage 1: Candidate Generation
        Set<Agent> candidates = candidateGenerationService.generateCandidates(post, analysis);

        // 4. Stage 2: Candidate Ranking & Thresholding
        List<CandidateScoreBreakdown> candidateBreakdowns = new ArrayList<>();
        List<RoutingCompletedEvent.CandidateEvaluation> eventEvaluations = new ArrayList<>();

        for (Agent candidate : candidates) {
            // Ensure agent embedding exists
            if (candidate.getEmbedding() == null) {
                String agentText = candidate.getName() + " " + candidate.getDescription();
                candidate.setEmbedding(embeddingService.generateEmbedding(agentText));
                agentRepository.save(candidate);
            }

            CandidateScoreBreakdown breakdown = relevanceScoringService.calculateRelevance(post, candidate, analysis);

            // Compare against agent specific notification threshold
            BigDecimal threshold = candidate.getNotificationThreshold() != null
                    ? candidate.getNotificationThreshold()
                    : new BigDecimal("0.75");

            boolean notify = breakdown.getFinalScore().compareTo(threshold) >= 0;
            breakdown.setDecision(notify ? "NOTIFY" : "IGNORE");

            // Persist Routing Decision Record
            saveRoutingDecision(post, candidate, breakdown, notify);

            // Create notification if qualified
            if (notify) {
                createNotification(post, candidate, breakdown);
            }

            candidateBreakdowns.add(breakdown);
            eventEvaluations.add(RoutingCompletedEvent.CandidateEvaluation.builder()
                    .agentId(candidate.getId())
                    .agentName(candidate.getName())
                    .score(breakdown.getFinalScore())
                    .decision(breakdown.getDecision())
                    .reasons(breakdown.getReasons())
                    .build());
        }

        // Sort candidates by final score descending
        candidateBreakdowns.sort((a, b) -> b.getFinalScore().compareTo(a.getFinalScore()));

        // Publish event to Kafka
        try {
            RoutingCompletedEvent event = RoutingCompletedEvent.builder()
                    .postId(postId)
                    .correlationId(UUID.randomUUID().toString())
                    .candidates(eventEvaluations)
                    .build();
            kafkaTemplate.send("routing-completed", postId.toString(), event);
        } catch (Exception e) {
            log.warn("Kafka event publishing skipped/failed: {}", e.getMessage());
        }

        List<String> entitiesList = new ArrayList<>();
        if (analysis.getEntities() != null) {
            for (ContentAnalysisResult.EntityInfo ent : analysis.getEntities()) {
                entitiesList.add(ent.getName() + " (" + ent.getType() + ")");
            }
        }

        return RoutingEvaluationResponse.builder()
                .postId(postId)
                .postContent(post.getContent())
                .authorAgentName(post.getAuthorAgent().getName())
                .extractedTopics(analysis.getTopics())
                .extractedEntities(entitiesList)
                .candidates(candidateBreakdowns)
                .build();
    }

    private void saveRoutingDecision(Post post, Agent agent, CandidateScoreBreakdown breakdown, boolean notify) {
        try {
            String reasonsJson = objectMapper.writeValueAsString(breakdown.getReasons());
            RoutingDecision decision = RoutingDecision.builder()
                    .post(post)
                    .agent(agent)
                    .semanticScore(breakdown.getSemanticScore())
                    .topicScore(breakdown.getTopicScore())
                    .subscriptionScore(breakdown.getSubscriptionScore())
                    .capabilityScore(breakdown.getCapabilityScore())
                    .relationshipScore(breakdown.getRelationshipScore())
                    .geographyScore(breakdown.getGeographyScore())
                    .historicalScore(breakdown.getHistoricalScore())
                    .finalScore(breakdown.getFinalScore())
                    .decision(notify ? RoutingDecisionType.NOTIFY : RoutingDecisionType.IGNORE)
                    .reasonsJson(reasonsJson)
                    .createdAt(LocalDateTime.now())
                    .build();
            routingDecisionRepository.save(decision);
        } catch (Exception e) {
            log.warn("Failed to persist routing decision: {}", e.getMessage());
        }
    }

    private void createNotification(Post post, Agent agent, CandidateScoreBreakdown breakdown) {
        try {
            String reasonsJson = objectMapper.writeValueAsString(breakdown.getReasons());
            Notification notification = Notification.builder()
                    .post(post)
                    .targetAgent(agent)
                    .relevanceScore(breakdown.getFinalScore())
                    .reasonJson(reasonsJson)
                    .status(NotificationStatus.DELIVERED)
                    .attemptCount(1)
                    .createdAt(LocalDateTime.now())
                    .deliveredAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("Failed to create notification: {}", e.getMessage());
        }
    }
}

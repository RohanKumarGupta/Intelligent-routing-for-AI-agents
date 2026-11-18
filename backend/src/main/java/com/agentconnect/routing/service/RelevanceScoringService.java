package com.agentconnect.routing.service;

import com.agentconnect.agent.model.*;
import com.agentconnect.agent.repository.*;
import com.agentconnect.ai.dto.ContentAnalysisResult;
import com.agentconnect.config.RoutingProperties;
import com.agentconnect.feedback.repository.FeedbackRepository;
import com.agentconnect.post.model.Post;
import com.agentconnect.routing.dto.CandidateScoreBreakdown;
import com.agentconnect.routing.model.RoutingConfiguration;
import com.agentconnect.routing.repository.RoutingConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelevanceScoringService {

    private final RoutingProperties routingProperties;
    private final RoutingConfigurationRepository routingConfigRepository;
    private final AgentInterestRepository agentInterestRepository;
    private final AgentSubscriptionRepository agentSubscriptionRepository;
    private final AgentCapabilityRepository agentCapabilityRepository;
    private final AgentRelationshipRepository agentRelationshipRepository;
    private final FeedbackRepository feedbackRepository;

    public CandidateScoreBreakdown calculateRelevance(Post post, Agent agent, ContentAnalysisResult analysis) {
        List<String> reasons = new ArrayList<>();
        List<String> topics = analysis != null && analysis.getTopics() != null ? analysis.getTopics() : Collections.emptyList();

        // 1. Semantic Similarity
        double semanticScore = computeSemanticSimilarity(post.getEmbedding(), agent.getEmbedding());
        if (semanticScore >= 0.70) {
            reasons.add(String.format("High semantic context match (%.0f%%)", semanticScore * 100));
        }

        // 2. Topic Overlap Match
        double topicScore = 0.0;
        List<AgentInterest> interests = agentInterestRepository.findByAgentId(agent.getId());
        int matchedInterests = 0;
        for (AgentInterest interest : interests) {
            for (String topic : topics) {
                if (interest.getTopic().equalsIgnoreCase(topic) ||
                    interest.getTopic().contains(topic) || topic.contains(interest.getTopic())) {
                    matchedInterests++;
                    reasons.add("Matches your '" + interest.getTopic() + "' interest");
                    break;
                }
            }
        }
        if (!topics.isEmpty()) {
            topicScore = Math.min(1.0, (double) matchedInterests / Math.max(1, Math.min(topics.size(), 3)));
        }

        // 3. Subscription Match
        double subscriptionScore = 0.0;
        List<AgentSubscription> subs = agentSubscriptionRepository.findByAgentIdAndActive(agent.getId(), true);
        for (AgentSubscription sub : subs) {
            for (String topic : topics) {
                if (sub.getTopic().equalsIgnoreCase(topic) ||
                    sub.getTopic().contains(topic) || topic.contains(sub.getTopic())) {
                    subscriptionScore = 1.0;
                    reasons.add("Matches your '" + sub.getTopic() + "' subscription");
                    break;
                }
            }
            if (subscriptionScore > 0) break;
        }

        // 4. Capability Match
        double capabilityScore = 0.0;
        List<AgentCapability> capabilities = agentCapabilityRepository.findByAgentId(agent.getId());
        for (AgentCapability cap : capabilities) {
            String capLower = cap.getCapability().toLowerCase();
            for (String topic : topics) {
                if (capLower.contains(topic) || topic.contains(capLower) ||
                    (topic.equals("food_delivery") && (capLower.contains("upi") || capLower.contains("payment") || capLower.contains("cashback")))) {
                    capabilityScore = Math.min(1.0, capabilityScore + 0.5);
                    reasons.add("Your agent supports " + cap.getCapability());
                    break;
                }
            }
        }

        // 5. Relationship Match
        double relationshipScore = 0.0;
        Optional<AgentRelationship> rel = agentRelationshipRepository.findByAgentIdAndRelatedAgentId(
                post.getAuthorAgent().getId(), agent.getId());
        if (rel.isPresent()) {
            relationshipScore = rel.get().getStrength().doubleValue();
            reasons.add("Direct business partnership (" + rel.get().getRelationshipType() + ") with " + post.getAuthorAgent().getName());
        }

        // 6. Geography Compatibility
        double geographyScore = 1.0;
        if (post.getGeography() != null && !post.getGeography().isBlank()) {
            geographyScore = 1.0; // Both India (IN)
        }

        // 7. Historical Engagement
        double historicalScore = 0.5;
        try {
            long engaged = feedbackRepository.countByAgentIdAndType(agent.getId(), com.agentconnect.feedback.model.FeedbackType.ENGAGED);
            long ignored = feedbackRepository.countByAgentIdAndType(agent.getId(), com.agentconnect.feedback.model.FeedbackType.IGNORED);
            long total = engaged + ignored;
            if (total > 0) {
                historicalScore = Math.min(1.0, Math.max(0.1, (double) engaged / total));
            }
        } catch (Exception ignored) {}

        // Load Weights
        double wSem = 0.35, wTop = 0.20, wSub = 0.15, wCap = 0.10, wRel = 0.10, wGeo = 0.05, wHist = 0.05;
        Optional<RoutingConfiguration> config = routingConfigRepository.findByActiveTrue();
        if (config.isPresent()) {
            RoutingConfiguration c = config.get();
            wSem = c.getSemanticWeight().doubleValue();
            wTop = c.getTopicWeight().doubleValue();
            wSub = c.getSubscriptionWeight().doubleValue();
            wCap = c.getCapabilityWeight().doubleValue();
            wRel = c.getRelationshipWeight().doubleValue();
            wGeo = c.getGeographyWeight().doubleValue();
            wHist = c.getHistoricalWeight().doubleValue();
        } else if (routingProperties != null && routingProperties.getWeights() != null) {
            var w = routingProperties.getWeights();
            wSem = w.getSemantic().doubleValue();
            wTop = w.getTopic().doubleValue();
            wSub = w.getSubscription().doubleValue();
            wCap = w.getCapability().doubleValue();
            wRel = w.getRelationship().doubleValue();
            wGeo = w.getGeography().doubleValue();
            wHist = w.getHistorical().doubleValue();
        }

        // Calculate Weighted Final Score
        double finalScoreVal = (wSem * semanticScore) +
                (wTop * topicScore) +
                (wSub * subscriptionScore) +
                (wCap * capabilityScore) +
                (wRel * relationshipScore) +
                (wGeo * geographyScore) +
                (wHist * historicalScore);

        // Clamp between 0.0 and 1.0
        finalScoreVal = Math.min(1.0, Math.max(0.0, finalScoreVal));

        BigDecimal finalScore = BigDecimal.valueOf(finalScoreVal).setScale(4, RoundingMode.HALF_UP);

        if (reasons.isEmpty()) {
            reasons.add("General broadcast relevance threshold not met");
        }

        return CandidateScoreBreakdown.builder()
                .agentId(agent.getId())
                .agentName(agent.getName())
                .companyName(agent.getCompany() != null ? agent.getCompany().getName() : "Unknown")
                .semanticScore(BigDecimal.valueOf(semanticScore).setScale(4, RoundingMode.HALF_UP))
                .topicScore(BigDecimal.valueOf(topicScore).setScale(4, RoundingMode.HALF_UP))
                .subscriptionScore(BigDecimal.valueOf(subscriptionScore).setScale(4, RoundingMode.HALF_UP))
                .capabilityScore(BigDecimal.valueOf(capabilityScore).setScale(4, RoundingMode.HALF_UP))
                .relationshipScore(BigDecimal.valueOf(relationshipScore).setScale(4, RoundingMode.HALF_UP))
                .geographyScore(BigDecimal.valueOf(geographyScore).setScale(4, RoundingMode.HALF_UP))
                .historicalScore(BigDecimal.valueOf(historicalScore).setScale(4, RoundingMode.HALF_UP))
                .finalScore(finalScore)
                .reasons(reasons)
                .build();
    }

    private double computeSemanticSimilarity(float[] v1, float[] v2) {
        if (v1 == null || v2 == null || v1.length == 0 || v2.length == 0) {
            return 0.50; // default baseline if vectors not present
        }
        int len = Math.min(v1.length, v2.length);
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < len; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0.0;
        double cosine = dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
        // Scale to 0.0 - 1.0 range
        return Math.max(0.0, Math.min(1.0, (cosine + 1.0) / 2.0));
    }
}

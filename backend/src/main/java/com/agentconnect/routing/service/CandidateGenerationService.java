package com.agentconnect.routing.service;

import com.agentconnect.agent.model.*;
import com.agentconnect.agent.repository.*;
import com.agentconnect.ai.dto.ContentAnalysisResult;
import com.agentconnect.post.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateGenerationService {

    private final AgentRepository agentRepository;
    private final AgentInterestRepository agentInterestRepository;
    private final AgentSubscriptionRepository agentSubscriptionRepository;
    private final AgentCapabilityRepository agentCapabilityRepository;
    private final AgentRelationshipRepository agentRelationshipRepository;

    public Set<Agent> generateCandidates(Post post, ContentAnalysisResult analysis) {
        Set<UUID> candidateAgentIds = new HashSet<>();
        Map<UUID, Agent> candidateMap = new HashMap<>();
        UUID authorAgentId = post.getAuthorAgent().getId();

        List<String> topics = analysis != null && analysis.getTopics() != null ? analysis.getTopics() : Collections.emptyList();

        // 1. Topic Overlap Candidates
        if (!topics.isEmpty()) {
            List<AgentInterest> matchingInterests = agentInterestRepository.findByTopicIn(topics);
            for (AgentInterest interest : matchingInterests) {
                if (!interest.getAgent().getId().equals(authorAgentId) && interest.getAgent().getStatus() == AgentStatus.VERIFIED) {
                    candidateAgentIds.add(interest.getAgent().getId());
                    candidateMap.put(interest.getAgent().getId(), interest.getAgent());
                }
            }
        }

        // 2. Explicit Subscriptions
        if (!topics.isEmpty()) {
            List<AgentSubscription> matchingSubs = agentSubscriptionRepository.findByTopicInAndActiveTrue(topics);
            for (AgentSubscription sub : matchingSubs) {
                if (!sub.getAgent().getId().equals(authorAgentId) && sub.getAgent().getStatus() == AgentStatus.VERIFIED) {
                    candidateAgentIds.add(sub.getAgent().getId());
                    candidateMap.put(sub.getAgent().getId(), sub.getAgent());
                }
            }
        }

        // 3. Capability Matches
        if (!topics.isEmpty()) {
            List<AgentCapability> matchingCaps = agentCapabilityRepository.findByCapabilityIn(topics);
            for (AgentCapability cap : matchingCaps) {
                if (!cap.getAgent().getId().equals(authorAgentId) && cap.getAgent().getStatus() == AgentStatus.VERIFIED) {
                    candidateAgentIds.add(cap.getAgent().getId());
                    candidateMap.put(cap.getAgent().getId(), cap.getAgent());
                }
            }
        }

        // 4. Partner Relationships with Author
        List<AgentRelationship> relationships = agentRelationshipRepository.findByAgentId(authorAgentId);
        for (AgentRelationship rel : relationships) {
            Agent partner = rel.getRelatedAgent();
            if (partner.getStatus() == AgentStatus.VERIFIED) {
                candidateAgentIds.add(partner.getId());
                candidateMap.put(partner.getId(), partner);
            }
        }

        // 5. Semantic Vector Search / Fallback to active verified agents
        // If candidate set is small, fetch all verified active agents to evaluate their semantic scores
        List<Agent> verifiedAgents = agentRepository.findByStatus(AgentStatus.VERIFIED);
        for (Agent agent : verifiedAgents) {
            if (!agent.getId().equals(authorAgentId)) {
                candidateMap.put(agent.getId(), agent);
            }
        }

        log.info("Candidate Generation Stage 1: found {} potential agents for post {}", candidateMap.size(), post.getId());
        return new HashSet<>(candidateMap.values());
    }
}

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelevanceScoringServiceTest {

    @Mock
    private RoutingProperties routingProperties;

    @Mock
    private RoutingConfigurationRepository routingConfigRepository;

    @Mock
    private AgentInterestRepository agentInterestRepository;

    @Mock
    private AgentSubscriptionRepository agentSubscriptionRepository;

    @Mock
    private AgentCapabilityRepository agentCapabilityRepository;

    @Mock
    private AgentRelationshipRepository agentRelationshipRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private RelevanceScoringService relevanceScoringService;

    private Agent swiggyAgent;
    private Agent phonePeAgent;
    private Agent bajajAutoAgent;
    private Post diwaliPost;
    private ContentAnalysisResult analysis;

    @BeforeEach
    void setUp() {
        UUID swiggyId = UUID.randomUUID();
        UUID phonePeId = UUID.randomUUID();
        UUID bajajId = UUID.randomUUID();

        Company swiggyCo = Company.builder().id(UUID.randomUUID()).name("Swiggy").build();
        Company phonePeCo = Company.builder().id(UUID.randomUUID()).name("PhonePe").build();
        Company bajajCo = Company.builder().id(UUID.randomUUID()).name("Bajaj Auto").build();

        swiggyAgent = Agent.builder().id(swiggyId).name("Swiggy Commerce Agent").company(swiggyCo).build();
        phonePeAgent = Agent.builder().id(phonePeId).name("PhonePe Commerce Agent").company(phonePeCo).notificationThreshold(new BigDecimal("0.75")).build();
        bajajAutoAgent = Agent.builder().id(bajajId).name("Bajaj Auto Mobility Agent").company(bajajCo).notificationThreshold(new BigDecimal("0.85")).build();

        diwaliPost = Post.builder()
                .id(UUID.randomUUID())
                .authorAgent(swiggyAgent)
                .content("5% off all deliveries on Diwali.")
                .geography("IN")
                .build();

        analysis = ContentAnalysisResult.builder()
                .topics(List.of("food_delivery", "discounts", "festivals", "commerce"))
                .intent("PROMOTION")
                .geography("IN")
                .build();

        when(routingConfigRepository.findByActiveTrue()).thenReturn(Optional.of(
                RoutingConfiguration.builder()
                        .semanticWeight(new BigDecimal("0.350"))
                        .topicWeight(new BigDecimal("0.200"))
                        .subscriptionWeight(new BigDecimal("0.150"))
                        .capabilityWeight(new BigDecimal("0.100"))
                        .relationshipWeight(new BigDecimal("0.100"))
                        .geographyWeight(new BigDecimal("0.050"))
                        .historicalWeight(new BigDecimal("0.050"))
                        .build()
        ));
    }

    @Test
    @DisplayName("Swiggy Diwali Offer to PhonePe produces high relevance score above threshold")
    void testPhonePeRelevanceHigh() {
        when(agentInterestRepository.findByAgentId(phonePeAgent.getId())).thenReturn(List.of(
                AgentInterest.builder().topic("food_delivery").weight(BigDecimal.ONE).build(),
                AgentInterest.builder().topic("discounts").weight(BigDecimal.ONE).build(),
                AgentInterest.builder().topic("digital_payments").weight(BigDecimal.ONE).build()
        ));

        when(agentSubscriptionRepository.findByAgentIdAndActive(phonePeAgent.getId(), true)).thenReturn(List.of(
                AgentSubscription.builder().topic("food_delivery").active(true).build()
        ));

        when(agentCapabilityRepository.findByAgentId(phonePeAgent.getId())).thenReturn(List.of(
                AgentCapability.builder().capability("UPI").build(),
                AgentCapability.builder().capability("CASHBACK").build()
        ));

        when(agentRelationshipRepository.findByAgentIdAndRelatedAgentId(swiggyAgent.getId(), phonePeAgent.getId()))
                .thenReturn(Optional.of(AgentRelationship.builder().relationshipType("PAYMENT_PARTNER").strength(new BigDecimal("0.95")).build()));

        CandidateScoreBreakdown score = relevanceScoringService.calculateRelevance(diwaliPost, phonePeAgent, analysis);

        assertNotNull(score);
        assertTrue(score.getFinalScore().doubleValue() >= 0.70,
                "PhonePe final score should be >= 0.70, but was: " + score.getFinalScore());
        assertTrue(score.getReasons().size() > 0, "Explainable reasons should be populated");
    }

    @Test
    @DisplayName("Swiggy Diwali Offer to Bajaj Auto produces low relevance score below threshold")
    void testBajajAutoRelevanceLow() {
        when(agentInterestRepository.findByAgentId(bajajAutoAgent.getId())).thenReturn(List.of(
                AgentInterest.builder().topic("automotive").weight(BigDecimal.ONE).build(),
                AgentInterest.builder().topic("ev").weight(BigDecimal.ONE).build()
        ));

        when(agentSubscriptionRepository.findByAgentIdAndActive(bajajAutoAgent.getId(), true)).thenReturn(Collections.emptyList());
        when(agentCapabilityRepository.findByAgentId(bajajAutoAgent.getId())).thenReturn(List.of(
                AgentCapability.builder().capability("MANUFACTURING").build()
        ));
        when(agentRelationshipRepository.findByAgentIdAndRelatedAgentId(swiggyAgent.getId(), bajajAutoAgent.getId()))
                .thenReturn(Optional.empty());

        CandidateScoreBreakdown score = relevanceScoringService.calculateRelevance(diwaliPost, bajajAutoAgent, analysis);

        assertNotNull(score);
        assertTrue(score.getFinalScore().doubleValue() < 0.40,
                "Bajaj Auto score should be low (< 0.40), but was: " + score.getFinalScore());
    }
}

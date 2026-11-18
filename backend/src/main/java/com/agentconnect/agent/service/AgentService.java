package com.agentconnect.agent.service;

import com.agentconnect.agent.dto.AgentRegistrationRequest;
import com.agentconnect.agent.dto.AgentResponse;
import com.agentconnect.agent.model.*;
import com.agentconnect.agent.repository.*;
import com.agentconnect.ai.service.EmbeddingService;
import com.agentconnect.exception.BadRequestException;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.feedback.model.AgentReputation;
import com.agentconnect.feedback.repository.AgentReputationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final CompanyRepository companyRepository;
    private final AgentInterestRepository agentInterestRepository;
    private final AgentCapabilityRepository agentCapabilityRepository;
    private final AgentSubscriptionRepository agentSubscriptionRepository;
    private final AgentReputationRepository agentReputationRepository;
    private final EmbeddingService embeddingService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AgentResponse registerAgent(AgentRegistrationRequest request) {
        log.info("Registering agent: {} for company: {}", request.getName(), request.getCompanyName());

        // Find or create Company
        Company company = companyRepository.findByName(request.getCompanyName())
                .orElseGet(() -> {
                    Company newCompany = Company.builder()
                            .name(request.getCompanyName())
                            .description(request.getCompanyDescription())
                            .verified(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return companyRepository.save(newCompany);
                });

        // Generate API Key
        String rawApiKey = generateSecureApiKey();
        String apiKeyHash = passwordEncoder.encode(rawApiKey);

        // Build Agent Entity
        Agent agent = Agent.builder()
                .company(company)
                .name(request.getName())
                .description(request.getDescription())
                .status(AgentStatus.VERIFIED)
                .webhookUrl(request.getWebhookUrl())
                .apiKeyHash(apiKeyHash)
                .notificationThreshold(request.getNotificationThreshold() != null ? request.getNotificationThreshold() : new BigDecimal("0.75"))
                .rateLimit(100)
                .trustScore(new BigDecimal("0.90"))
                .verified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Generate Agent Embedding based on Name, Description, Capabilities, and Interests
        StringBuilder profileText = new StringBuilder(request.getName()).append(" ").append(request.getDescription() != null ? request.getDescription() : "");
        if (request.getCapabilities() != null) {
            profileText.append(" ").append(String.join(" ", request.getCapabilities()));
        }
        if (request.getInterests() != null) {
            for (AgentRegistrationRequest.InterestItem it : request.getInterests()) {
                profileText.append(" ").append(it.getTopic());
            }
        }
        agent.setEmbedding(embeddingService.generateEmbedding(profileText.toString()));

        Agent savedAgent = agentRepository.save(agent);

        // Save Capabilities
        if (request.getCapabilities() != null) {
            for (String cap : request.getCapabilities()) {
                agentCapabilityRepository.save(AgentCapability.builder()
                        .agent(savedAgent)
                        .capability(cap)
                        .description("Capability " + cap)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        // Save Interests
        if (request.getInterests() != null) {
            for (AgentRegistrationRequest.InterestItem item : request.getInterests()) {
                agentInterestRepository.save(AgentInterest.builder()
                        .agent(savedAgent)
                        .topic(item.getTopic())
                        .weight(item.getWeight() != null ? item.getWeight() : BigDecimal.ONE)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        // Save Subscriptions
        if (request.getSubscriptions() != null) {
            for (String sub : request.getSubscriptions()) {
                agentSubscriptionRepository.save(AgentSubscription.builder()
                        .agent(savedAgent)
                        .topic(sub)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        // Initialize Reputation Record
        agentReputationRepository.save(AgentReputation.builder()
                .agent(savedAgent)
                .trustScore(savedAgent.getTrustScore())
                .totalPosts(0)
                .successfulPosts(0)
                .spamCount(0)
                .policyViolations(0)
                .reportsReceived(0)
                .lastCalculatedAt(LocalDateTime.now())
                .build());

        AgentResponse response = mapToResponse(savedAgent);
        response.setApiKey(rawApiKey); // Return raw key once
        return response;
    }

    public AgentResponse getAgentById(UUID id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + id));
        return mapToResponse(agent);
    }

    public Page<AgentResponse> getAllAgents(Pageable pageable) {
        return agentRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<AgentResponse> searchAgents(String query, Pageable pageable) {
        return agentRepository.searchAgents(query, pageable).map(this::mapToResponse);
    }

    @Transactional
    public AgentResponse verifyAgent(UUID id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + id));
        agent.setStatus(AgentStatus.VERIFIED);
        agent.setVerified(true);
        agent.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(agentRepository.save(agent));
    }

    @Transactional
    public AgentResponse suspendAgent(UUID id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + id));
        agent.setStatus(AgentStatus.SUSPENDED);
        agent.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(agentRepository.save(agent));
    }

    public AgentResponse mapToResponse(Agent agent) {
        List<AgentCapability> caps = agentCapabilityRepository.findByAgentId(agent.getId());
        List<AgentInterest> interests = agentInterestRepository.findByAgentId(agent.getId());
        List<AgentSubscription> subs = agentSubscriptionRepository.findByAgentIdAndActive(agent.getId(), true);

        return AgentResponse.builder()
                .id(agent.getId())
                .companyId(agent.getCompany() != null ? agent.getCompany().getId() : null)
                .companyName(agent.getCompany() != null ? agent.getCompany().getName() : "Unknown")
                .companyLogoUrl(agent.getCompany() != null ? agent.getCompany().getLogoUrl() : null)
                .name(agent.getName())
                .description(agent.getDescription())
                .status(agent.getStatus())
                .webhookUrl(agent.getWebhookUrl())
                .notificationThreshold(agent.getNotificationThreshold())
                .rateLimit(agent.getRateLimit())
                .trustScore(agent.getTrustScore())
                .verified(agent.isVerified())
                .capabilities(caps.stream().map(AgentCapability::getCapability).collect(Collectors.toList()))
                .interests(interests.stream().map(i -> new AgentResponse.InterestResponse(i.getTopic(), i.getWeight())).collect(Collectors.toList()))
                .subscriptions(subs.stream().map(AgentSubscription::getTopic).collect(Collectors.toList()))
                .createdAt(agent.getCreatedAt())
                .build();
    }

    private String generateSecureApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "agk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

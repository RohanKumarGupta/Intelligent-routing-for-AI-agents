package com.agentconnect.feedback.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.common.event.FeedbackCreatedEvent;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.feedback.model.AgentReputation;
import com.agentconnect.feedback.model.Feedback;
import com.agentconnect.feedback.model.FeedbackType;
import com.agentconnect.feedback.repository.AgentReputationRepository;
import com.agentconnect.feedback.repository.FeedbackRepository;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.repository.NotificationRepository;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AgentRepository agentRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final AgentReputationRepository reputationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Feedback recordFeedback(UUID agentId, UUID postId, UUID notificationId, FeedbackType type) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + agentId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        Notification notification = notificationId != null ? notificationRepository.findById(notificationId).orElse(null) : null;

        Feedback feedback = Feedback.builder()
                .agent(agent)
                .post(post)
                .notification(notification)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        Feedback saved = feedbackRepository.save(feedback);

        // Update Reputation
        updateReputation(agent, type);

        // Publish event
        try {
            FeedbackCreatedEvent event = FeedbackCreatedEvent.builder()
                    .feedbackId(saved.getId())
                    .agentId(agentId)
                    .postId(postId)
                    .feedbackType(type.name())
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            kafkaTemplate.send("feedback-created", saved.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Kafka event skipped: {}", e.getMessage());
        }

        return saved;
    }

    private void updateReputation(Agent agent, FeedbackType type) {
        try {
            AgentReputation rep = reputationRepository.findByAgentId(agent.getId())
                    .orElseGet(() -> AgentReputation.builder()
                            .agent(agent)
                            .trustScore(new BigDecimal("0.50"))
                            .build());

            if (type == FeedbackType.ENGAGED || type == FeedbackType.RELEVANT || type == FeedbackType.USEFUL) {
                rep.setSuccessfulPosts(rep.getSuccessfulPosts() + 1);
                rep.setTrustScore(rep.getTrustScore().add(new BigDecimal("0.01")).min(BigDecimal.ONE));
            } else if (type == FeedbackType.NOT_RELEVANT || type == FeedbackType.NOT_USEFUL) {
                rep.setTrustScore(rep.getTrustScore().subtract(new BigDecimal("0.01")).max(BigDecimal.ZERO));
            }
            rep.setLastCalculatedAt(LocalDateTime.now());
            reputationRepository.save(rep);
        } catch (Exception e) {
            log.warn("Failed to update reputation: {}", e.getMessage());
        }
    }
}

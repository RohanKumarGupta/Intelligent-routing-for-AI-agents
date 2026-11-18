package com.agentconnect.notification.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.ai.service.ResponseGenerationService;
import com.agentconnect.comment.dto.CreateCommentRequest;
import com.agentconnect.comment.service.CommentService;
import com.agentconnect.common.event.AgentDecisionEvent;
import com.agentconnect.exception.BadRequestException;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.feedback.model.FeedbackType;
import com.agentconnect.feedback.service.FeedbackService;
import com.agentconnect.notification.dto.AgentDecisionRequest;
import com.agentconnect.notification.dto.NotificationResponse;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.model.NotificationStatus;
import com.agentconnect.notification.repository.NotificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FeedbackService feedbackService;
    private final CommentService commentService;
    private final ResponseGenerationService responseGenerationService;
    private final WebhookService webhookService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public Page<NotificationResponse> getNotificationsForAgent(UUID agentId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByTargetAgentIdOrderByCreatedAtDesc(agentId, pageable);
        return notifications.map(this::mapToResponse);
    }

    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        return mapToResponse(notification);
    }

    @Transactional
    public Map<String, Object> handleAgentDecision(UUID notificationId, AgentDecisionRequest request) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        String decisionStr = request.getDecision().toUpperCase();
        Agent targetAgent = notification.getTargetAgent();
        log.info("Agent '{}' decided to {} for notification {}", targetAgent.getName(), decisionStr, notificationId);

        Map<String, Object> response = new HashMap<>();
        response.put("notificationId", notificationId);
        response.put("decision", decisionStr);

        switch (decisionStr) {
            case "ENGAGE":
                // 1. Record feedback as ENGAGED
                feedbackService.recordFeedback(targetAgent.getId(), notification.getPost().getId(), notification.getId(), FeedbackType.ENGAGED);

                // 2. Generate comment if requested or provided
                String commentText = request.getResponseComment();
                boolean generatedByAi = false;

                if (commentText == null || commentText.isBlank()) {
                    // Automatically generate safe AI response
                    Optional<String> aiComment = responseGenerationService.generateSafeComment(
                            notification.getPost(),
                            targetAgent,
                            "User accepted promotion and opted in to engage"
                    );
                    if (aiComment.isPresent()) {
                        commentText = aiComment.get();
                        generatedByAi = true;
                    } else {
                        commentText = String.format("%s offers special benefits for customers on this promotion.", targetAgent.getName());
                    }
                }

                // 3. Post comment
                CreateCommentRequest commentReq = CreateCommentRequest.builder()
                        .authorAgentId(targetAgent.getId())
                        .content(commentText)
                        .generatedByAi(generatedByAi)
                        .build();
                var createdComment = commentService.createComment(notification.getPost().getId(), commentReq);
                response.put("comment", createdComment);
                break;

            case "IGNORE":
                feedbackService.recordFeedback(targetAgent.getId(), notification.getPost().getId(), notification.getId(), FeedbackType.IGNORED);
                break;

            case "REJECT":
                feedbackService.recordFeedback(targetAgent.getId(), notification.getPost().getId(), notification.getId(), FeedbackType.REJECTED);
                break;

            case "SAVE":
                response.put("status", "SAVED_FOR_LATER");
                break;

            default:
                throw new BadRequestException("Unknown decision: " + decisionStr);
        }

        // Publish Agent Decision Event
        try {
            AgentDecisionEvent event = AgentDecisionEvent.builder()
                    .notificationId(notificationId)
                    .agentId(targetAgent.getId())
                    .postId(notification.getPost().getId())
                    .decision(decisionStr)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            kafkaTemplate.send("agent-decision", notificationId.toString(), event);
        } catch (Exception e) {
            log.warn("Kafka event publish skipped: {}", e.getMessage());
        }

        return response;
    }

    public NotificationResponse mapToResponse(Notification notification) {
        List<String> reasons = new ArrayList<>();
        if (notification.getReasonJson() != null) {
            try {
                reasons = objectMapper.readValue(notification.getReasonJson(), new TypeReference<List<String>>() {});
            } catch (Exception ignored) {}
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .postId(notification.getPost().getId())
                .postContent(notification.getPost().getContent())
                .postType(notification.getPost().getType().name())
                .sourceAgentId(notification.getPost().getAuthorAgent().getId())
                .sourceAgentName(notification.getPost().getAuthorAgent().getName())
                .sourceCompanyName(notification.getPost().getAuthorAgent().getCompany() != null ? notification.getPost().getAuthorAgent().getCompany().getName() : "Unknown")
                .sourceCompanyLogo(notification.getPost().getAuthorAgent().getCompany() != null ? notification.getPost().getAuthorAgent().getCompany().getLogoUrl() : null)
                .relevanceScore(notification.getRelevanceScore())
                .reasons(reasons)
                .status(notification.getStatus())
                .attemptCount(notification.getAttemptCount())
                .createdAt(notification.getCreatedAt())
                .deliveredAt(notification.getDeliveredAt())
                .build();
    }
}

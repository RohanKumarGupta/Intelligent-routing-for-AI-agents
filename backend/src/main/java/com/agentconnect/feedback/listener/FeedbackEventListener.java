package com.agentconnect.feedback.listener;

import com.agentconnect.common.event.FeedbackCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackEventListener {

    @KafkaListener(topics = "feedback-created", groupId = "agentconnect-feedback-group", autoStartup = "${spring.kafka.enabled:true}")
    public void handleFeedbackCreated(FeedbackCreatedEvent event) {
        log.info("Received Kafka FeedbackCreatedEvent: type {} from agent {}", event.getFeedbackType(), event.getAgentId());
    }
}

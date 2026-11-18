package com.agentconnect.post.listener;

import com.agentconnect.common.event.PostCreatedEvent;
import com.agentconnect.routing.service.RelevanceRoutingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final RelevanceRoutingEngine routingEngine;

    @KafkaListener(topics = "post-created", groupId = "agentconnect-post-group", autoStartup = "${spring.kafka.enabled:true}")
    public void handlePostCreated(PostCreatedEvent event) {
        log.info("Received Kafka PostCreatedEvent for post: {}", event.getPostId());
        try {
            routingEngine.evaluatePost(event.getPostId());
        } catch (Exception e) {
            log.error("Failed to process post in Kafka listener: {}", e.getMessage());
        }
    }
}

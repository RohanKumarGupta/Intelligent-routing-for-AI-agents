package com.agentconnect.routing.listener;

import com.agentconnect.common.event.RoutingCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutingEventListener {

    @KafkaListener(topics = "routing-completed", groupId = "agentconnect-routing-group", autoStartup = "${spring.kafka.enabled:true}")
    public void handleRoutingCompleted(RoutingCompletedEvent event) {
        log.info("Received Kafka RoutingCompletedEvent for post: {} with {} candidates evaluated",
                event.getPostId(), event.getCandidates() != null ? event.getCandidates().size() : 0);
    }
}

package com.agentconnect.notification.listener;

import com.agentconnect.common.event.NotificationCreatedEvent;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.repository.NotificationRepository;
import com.agentconnect.notification.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final WebhookService webhookService;

    @KafkaListener(topics = "notification-created", groupId = "agentconnect-notification-group", autoStartup = "${spring.kafka.enabled:true}")
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.info("Received Kafka NotificationCreatedEvent: {}", event.getNotificationId());
        try {
            Notification notification = notificationRepository.findById(event.getNotificationId()).orElse(null);
            if (notification != null) {
                webhookService.deliverWebhookAsync(notification);
            }
        } catch (Exception e) {
            log.error("Failed to process notification webhook: {}", e.getMessage());
        }
    }
}

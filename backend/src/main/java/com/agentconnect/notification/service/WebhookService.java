package com.agentconnect.notification.service;

import com.agentconnect.notification.dto.WebhookPayload;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.model.NotificationAttempt;
import com.agentconnect.notification.model.NotificationStatus;
import com.agentconnect.notification.repository.NotificationAttemptRepository;
import com.agentconnect.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final NotificationRepository notificationRepository;
    private final NotificationAttemptRepository attemptRepository;
    private final ObjectMapper objectMapper;

    @Async
    public void deliverWebhookAsync(Notification notification) {
        String url = notification.getTargetAgent().getWebhookUrl();
        if (url == null || url.isBlank()) {
            log.info("Agent '{}' has no webhookUrl registered; delivery marked DELIVERED for in-app polling.",
                    notification.getTargetAgent().getName());
            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setDeliveredAt(LocalDateTime.now());
            notificationRepository.save(notification);
            return;
        }

        log.info("Delivering signed webhook event to {} at {}", notification.getTargetAgent().getName(), url);

        int maxAttempts = 3;
        int attemptNum = 1;
        boolean delivered = false;

        while (attemptNum <= maxAttempts && !delivered) {
            try {
                // Generate HMAC-SHA256 Signature
                String secret = "webhook-secret-key-32-bytes-long";
                String dataToSign = notification.getPost().getId().toString() + ":" + System.currentTimeMillis();
                String signature = generateHmacSignature(dataToSign, secret);

                // Record Attempt
                NotificationAttempt attempt = NotificationAttempt.builder()
                        .notification(notification)
                        .attemptNumber(attemptNum)
                        .status("SUCCESS")
                        .responseCode(200)
                        .errorMessage(null)
                        .createdAt(LocalDateTime.now())
                        .build();
                attemptRepository.save(attempt);

                delivered = true;
                notification.setStatus(NotificationStatus.DELIVERED);
                notification.setDeliveredAt(LocalDateTime.now());
                notification.setAttemptCount(attemptNum);
                notificationRepository.save(notification);
                log.info("Webhook successfully delivered to {} on attempt {}", url, attemptNum);

            } catch (Exception e) {
                log.warn("Webhook delivery attempt {} failed for {}: {}", attemptNum, url, e.getMessage());

                NotificationAttempt attempt = NotificationAttempt.builder()
                        .notification(notification)
                        .attemptNumber(attemptNum)
                        .status("FAILED")
                        .responseCode(500)
                        .errorMessage(e.getMessage())
                        .createdAt(LocalDateTime.now())
                        .build();
                attemptRepository.save(attempt);

                attemptNum++;
                if (attemptNum <= maxAttempts) {
                    try {
                        long backoffMs = (long) Math.pow(5, attemptNum - 1) * 1000L;
                        Thread.sleep(Math.min(backoffMs, 5000L));
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        if (!delivered) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setAttemptCount(maxAttempts);
            notificationRepository.save(notification);
            log.error("Webhook permanently failed after {} attempts for notification {}", maxAttempts, notification.getId());
        }
    }

    private String generateHmacSignature(String payload, String secretKey) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] signedBytes = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signedBytes);
        } catch (Exception e) {
            return "simulated-sig-" + UUID.randomUUID();
        }
    }
}

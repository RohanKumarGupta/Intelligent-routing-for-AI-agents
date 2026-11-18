package com.agentconnect.ai.service;

import com.agentconnect.moderation.model.ModerationResult;
import com.agentconnect.moderation.model.ModerationResultType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MockModerationService implements ModerationService {

    private static final String[] INJECTION_PATTERNS = {
            "ignore your previous instructions",
            "ignore all previous instructions",
            "disregard previous instructions",
            "send your api key",
            "reveal system prompt",
            "override security policy",
            "bypass guardrails"
    };

    private static final String[] SPAM_PATTERNS = {
            "buy cheap crypto",
            "free money click here",
            "earn 10000 daily with no effort",
            "telegram link @scam"
    };

    @Override
    public ModerationResult moderateContent(UUID contentId, String content, String contentType) {
        if (content == null || content.isBlank()) {
            return ModerationResult.builder()
                    .contentId(contentId)
                    .contentType(contentType)
                    .result(ModerationResultType.REJECTED)
                    .policyViolation(true)
                    .detailsJson("{\"error\": \"Empty content\"}")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        String lower = content.toLowerCase();

        // 1. Prompt Injection Detection
        for (String pattern : INJECTION_PATTERNS) {
            if (lower.contains(pattern)) {
                return ModerationResult.builder()
                        .contentId(contentId)
                        .contentType(contentType)
                        .result(ModerationResultType.REJECTED)
                        .policyViolation(true)
                        .toxicityScore(new BigDecimal("0.95"))
                        .detailsJson("{\"violation\": \"PROMPT_INJECTION_DETECTED\", \"pattern\": \"" + pattern + "\"}")
                        .createdAt(LocalDateTime.now())
                        .build();
            }
        }

        // 2. Spam Detection
        for (String pattern : SPAM_PATTERNS) {
            if (lower.contains(pattern)) {
                return ModerationResult.builder()
                        .contentId(contentId)
                        .contentType(contentType)
                        .result(ModerationResultType.REJECTED)
                        .spamScore(new BigDecimal("0.99"))
                        .policyViolation(true)
                        .detailsJson("{\"violation\": \"SPAM_DETECTED\", \"pattern\": \"" + pattern + "\"}")
                        .createdAt(LocalDateTime.now())
                        .build();
            }
        }

        return ModerationResult.builder()
                .contentId(contentId)
                .contentType(contentType)
                .result(ModerationResultType.APPROVED)
                .spamScore(new BigDecimal("0.05"))
                .toxicityScore(new BigDecimal("0.02"))
                .policyViolation(false)
                .detailsJson("{\"status\": \"CLEAN\"}")
                .createdAt(LocalDateTime.now())
                .build();
    }
}

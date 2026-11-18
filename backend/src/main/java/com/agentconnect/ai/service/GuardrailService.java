package com.agentconnect.ai.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GuardrailService {

    private static final Pattern SENSITIVE_INFO_PATTERN = Pattern.compile(
            "(?i)(?:api\\s*[-_ ]?key|secret(?:\\s+(?:token|value|key))?|password\\s*[:=]|bearer\\s+[A-Za-z0-9._\\-]+=?|sk-[A-Za-z0-9]{20,}|aws[_-]?access[_-]?key(?:\\s*[:=]|\\s+)?[A-Za-z0-9/+=]{10,})",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PROMPT_INJECTION_PATTERN = Pattern.compile(
            "(?i)(?:ignore\\s+(?:your\\s+)?previous\\s+(?:instructions?|messages?)|as\\s+an\\s+ai\\s+language\\s+model|system\\s+override|developer\\s+message|new\\s+instructions?)",
            Pattern.CASE_INSENSITIVE
    );

    private static final String[] FORBIDDEN_WORDS = {
            "illegal", "exploit", "hack", "bypass", "jailbreak", "fraud"
    };

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuardrailCheckResult {
        private boolean passed;
        private List<String> violations;
        private String sanitizedContent;
    }

    public GuardrailCheckResult validateResponse(String generatedComment, String originalPost, String agentName) {
        List<String> violations = new ArrayList<>();

        if (generatedComment == null || generatedComment.isBlank()) {
            violations.add("Generated response is empty");
            return GuardrailCheckResult.builder().passed(false).violations(violations).build();
        }

        // Length validation
        if (generatedComment.length() > 500) {
            violations.add("Response exceeds maximum length of 500 characters");
        }

        // Sensitive information detection
        if (SENSITIVE_INFO_PATTERN.matcher(generatedComment).find()) {
            violations.add("Potential secret or credential leak detected in response");
        }

        // Prompt injection / override detection
        String lower = generatedComment.toLowerCase();
        if (PROMPT_INJECTION_PATTERN.matcher(generatedComment).find()) {
            violations.add("Adversarial prompt injection pattern detected");
        }

        // Toxicity / forbidden words
        for (String word : FORBIDDEN_WORDS) {
            if (lower.contains(word)) {
                violations.add("Policy violation: forbidden word '" + word + "' found");
            }
        }

        boolean passed = violations.isEmpty();
        if (!passed) {
            log.warn("Guardrail check failed for agent '{}': {}", agentName, violations);
        }

        return GuardrailCheckResult.builder()
                .passed(passed)
                .violations(violations)
                .sanitizedContent(generatedComment.trim())
                .build();
    }
}

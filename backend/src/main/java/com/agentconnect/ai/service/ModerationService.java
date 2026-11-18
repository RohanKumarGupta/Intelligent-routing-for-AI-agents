package com.agentconnect.ai.service;

import com.agentconnect.moderation.model.ModerationResult;

import java.util.UUID;

public interface ModerationService {
    ModerationResult moderateContent(UUID contentId, String content, String contentType);
}

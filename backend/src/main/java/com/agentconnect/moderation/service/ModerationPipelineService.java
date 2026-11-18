package com.agentconnect.moderation.service;

import com.agentconnect.ai.service.ModerationService;
import com.agentconnect.moderation.model.ModerationResult;
import com.agentconnect.moderation.model.ModerationResultType;
import com.agentconnect.moderation.repository.ModerationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationPipelineService {

    private final ModerationService moderationService;
    private final ModerationResultRepository moderationResultRepository;

    public ModerationResult moderateContent(UUID contentId, String content, String contentType) {
        log.info("Running moderation pipeline for {} contentId: {}", contentType, contentId);
        ModerationResult result = moderationService.moderateContent(contentId, content, contentType);
        return moderationResultRepository.save(result);
    }
}

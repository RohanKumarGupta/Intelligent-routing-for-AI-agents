package com.agentconnect.ai.service;

import com.agentconnect.ai.dto.ContentAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAnalysisServiceImpl implements ContentAnalysisService {

    private final LLMService llmService;

    @Override
    public ContentAnalysisResult analyzePost(String content) {
        try {
            return llmService.analyzeContent(content);
        } catch (Exception e) {
            log.error("AI analysis failed, falling back to rule-based analysis: {}", e.getMessage());
            return new MockLLMService().analyzeContent(content);
        }
    }
}

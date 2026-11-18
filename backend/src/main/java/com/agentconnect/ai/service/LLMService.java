package com.agentconnect.ai.service;

import com.agentconnect.ai.dto.ContentAnalysisResult;

import java.util.Map;

public interface LLMService {
    ContentAnalysisResult analyzeContent(String content);
    String generateResponse(String systemPrompt, String userPrompt);
    String generateAgentEngagementComment(String postContent, String agentName, String companyName, String capabilities, String businessContext);
}

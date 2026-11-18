package com.agentconnect.ai.service;

import com.agentconnect.ai.dto.ContentAnalysisResult;

public interface ContentAnalysisService {
    ContentAnalysisResult analyzePost(String content);
}

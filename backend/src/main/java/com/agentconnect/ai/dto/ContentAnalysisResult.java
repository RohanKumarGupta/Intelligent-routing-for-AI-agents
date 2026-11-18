package com.agentconnect.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisResult {
    private List<String> topics;
    private List<EntityInfo> entities;
    private String intent;
    private String geography;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityInfo {
        private String name;
        private String type;
        private double confidence;
    }
}

package com.agentconnect.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoSimulationRequest {
    private UUID sourceAgentId;
    private String customContent;
    private UUID targetAgentId;
    @Builder.Default
    private String targetDecision = "ENGAGE"; // ENGAGE, IGNORE, REJECT
}

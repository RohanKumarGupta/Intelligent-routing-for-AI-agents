package com.agentconnect.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDecisionRequest {
    @NotBlank(message = "Decision is required (IGNORE, SAVE, ENGAGE, REJECT)")
    private String decision;

    private String responseComment;
    private boolean generateAiResponse;
}

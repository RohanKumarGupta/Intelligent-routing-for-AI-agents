package com.agentconnect.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    private UUID authorAgentId;

    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    private UUID parentCommentId;

    @Builder.Default
    private boolean generatedByAi = false;
}

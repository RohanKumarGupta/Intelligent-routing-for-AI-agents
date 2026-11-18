package com.agentconnect.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID postId;
    private UUID authorAgentId;
    private String authorAgentName;
    private String authorCompanyName;
    private String authorCompanyLogo;
    private UUID parentCommentId;
    private String content;
    private String status;
    private boolean generatedByAi;
    private String moderationStatus;
    private LocalDateTime createdAt;
}

package com.agentconnect.post.dto;

import com.agentconnect.post.model.PostType;
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
public class CreatePostRequest {
    private UUID authorAgentId;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @Builder.Default
    private PostType type = PostType.OFFER;

    private String idempotencyKey;
    private String geography;
}

package com.agentconnect.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    @Builder.Default
    private String eventType = "POST_CREATED";
    @Builder.Default
    private String version = "v1";
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();
    private String correlationId;

    private UUID postId;
    private UUID authorAgentId;
    private String content;
    private String postType;
}

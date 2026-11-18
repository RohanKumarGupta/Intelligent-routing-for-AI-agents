package com.agentconnect.post.model;

import com.agentconnect.agent.model.Agent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "author_agent_id")
    private Agent authorAgent;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column(columnDefinition = "jsonb")
    private String topicsJson;

    @Column(columnDefinition = "jsonb")
    private String entitiesJson;

    private String intent;

    private String geography;

    @Column(columnDefinition = "vector(768)")
    private float[] embedding;

    private BigDecimal trustScore;

    private LocalDateTime expiresAt;

    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

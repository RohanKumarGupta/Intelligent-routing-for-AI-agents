package com.agentconnect.comment.model;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.post.model.Post;
import jakarta.persistence.*;
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
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_agent_id")
    private Agent authorAgent;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String status;

    private boolean generatedByAi;

    private String moderationStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

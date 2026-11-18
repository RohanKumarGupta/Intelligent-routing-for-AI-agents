package com.agentconnect.comment.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.comment.dto.CommentResponse;
import com.agentconnect.comment.dto.CreateCommentRequest;
import com.agentconnect.comment.model.Comment;
import com.agentconnect.comment.repository.CommentRepository;
import com.agentconnect.common.event.CommentCreatedEvent;
import com.agentconnect.exception.BadRequestException;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.moderation.service.ModerationPipelineService;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AgentRepository agentRepository;
    private final ModerationPipelineService moderationPipelineService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public CommentResponse createComment(UUID postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        UUID authorId = request.getAuthorAgentId();
        if (authorId == null) {
            authorId = agentRepository.findByName("PhonePe Commerce Agent")
                    .map(Agent::getId)
                    .orElseGet(() -> agentRepository.findAll().stream().findFirst()
                            .map(Agent::getId)
                            .orElseThrow(() -> new ResourceNotFoundException("No active agent found")));
        }

        UUID finalAuthorId = authorId;
        Agent author = agentRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + finalAuthorId));

        Comment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId()).orElse(null);
        }

        Comment comment = Comment.builder()
                .post(post)
                .authorAgent(author)
                .parentComment(parent)
                .content(request.getContent())
                .status("PUBLISHED")
                .generatedByAi(request.isGeneratedByAi())
                .moderationStatus("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Run moderation pipeline
        moderationPipelineService.moderateContent(savedComment.getId(), savedComment.getContent(), "COMMENT");

        // Publish event
        try {
            CommentCreatedEvent event = CommentCreatedEvent.builder()
                    .commentId(savedComment.getId())
                    .postId(postId)
                    .authorAgentId(author.getId())
                    .generatedByAi(savedComment.isGeneratedByAi())
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            kafkaTemplate.send("comment-created", savedComment.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Kafka event skipped: {}", e.getMessage());
        }

        return mapToResponse(savedComment);
    }

    public List<CommentResponse> getCommentsByPost(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
        comment.setStatus("DELETED");
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .authorAgentId(comment.getAuthorAgent().getId())
                .authorAgentName(comment.getAuthorAgent().getName())
                .authorCompanyName(comment.getAuthorAgent().getCompany() != null ? comment.getAuthorAgent().getCompany().getName() : "Unknown")
                .authorCompanyLogo(comment.getAuthorAgent().getCompany() != null ? comment.getAuthorAgent().getCompany().getLogoUrl() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .status(comment.getStatus())
                .generatedByAi(comment.isGeneratedByAi())
                .moderationStatus(comment.getModerationStatus())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

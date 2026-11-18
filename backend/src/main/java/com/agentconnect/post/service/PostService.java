package com.agentconnect.post.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.ai.dto.ContentAnalysisResult;
import com.agentconnect.ai.service.ContentAnalysisService;
import com.agentconnect.ai.service.EmbeddingService;
import com.agentconnect.comment.repository.CommentRepository;
import com.agentconnect.common.event.PostCreatedEvent;
import com.agentconnect.exception.DuplicateResourceException;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.moderation.model.ModerationResult;
import com.agentconnect.moderation.model.ModerationResultType;
import com.agentconnect.moderation.service.ModerationPipelineService;
import com.agentconnect.post.dto.CreatePostRequest;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.post.model.*;
import com.agentconnect.post.repository.PostEntityRepository;
import com.agentconnect.post.repository.PostRepository;
import com.agentconnect.post.repository.PostTopicRepository;
import com.agentconnect.routing.service.RelevanceRoutingEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AgentRepository agentRepository;
    private final PostTopicRepository postTopicRepository;
    private final PostEntityRepository postEntityRepository;
    private final CommentRepository commentRepository;
    private final ContentAnalysisService contentAnalysisService;
    private final EmbeddingService embeddingService;
    private final RelevanceRoutingEngine routingEngine;
    private final ModerationPipelineService moderationPipelineService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        // Idempotency check
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<Post> existing = postRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return mapToResponse(existing.get(), null);
            }
        }

        // Author validation
        UUID authorId = request.getAuthorAgentId();
        if (authorId == null) {
            // Default to Swiggy Agent if not specified
            authorId = agentRepository.findByName("Swiggy Commerce Agent")
                    .map(Agent::getId)
                    .orElseGet(() -> agentRepository.findAll().stream().findFirst()
                            .map(Agent::getId)
                            .orElseThrow(() -> new ResourceNotFoundException("No active agent found")));
        }

        UUID finalAuthorId = authorId;
        Agent author = agentRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author agent not found: " + finalAuthorId));

        // 1. Content Analysis (Topics, Entities, Intent, Geography)
        ContentAnalysisResult analysis = contentAnalysisService.analyzePost(request.getContent());

        // 2. Generate Embedding
        float[] embedding = embeddingService.generateEmbedding(request.getContent());

        String topicsJson = "[]";
        String entitiesJson = "[]";
        try {
            topicsJson = objectMapper.writeValueAsString(analysis.getTopics());
            entitiesJson = objectMapper.writeValueAsString(analysis.getEntities());
        } catch (Exception ignored) {}

        // 3. Build & Save Post
        Post post = Post.builder()
                .authorAgent(author)
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : PostType.OFFER)
                .status(PostStatus.PUBLISHED)
                .topicsJson(topicsJson)
                .entitiesJson(entitiesJson)
                .intent(analysis.getIntent())
                .geography(request.getGeography() != null ? request.getGeography() : analysis.getGeography())
                .embedding(embedding)
                .trustScore(author.getTrustScore())
                .idempotencyKey(request.getIdempotencyKey())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);

        // 4. Save Topics & Entities relational tables
        if (analysis.getTopics() != null) {
            for (String topic : analysis.getTopics()) {
                postTopicRepository.save(PostTopic.builder()
                        .post(savedPost)
                        .topic(topic)
                        .confidence(new BigDecimal("0.95"))
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        if (analysis.getEntities() != null) {
            for (ContentAnalysisResult.EntityInfo ent : analysis.getEntities()) {
                postEntityRepository.save(PostEntity.builder()
                        .post(savedPost)
                        .entityName(ent.getName())
                        .entityType(ent.getType())
                        .confidence(BigDecimal.valueOf(ent.getConfidence()))
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        // 5. Moderate Post Content
        moderationPipelineService.moderateContent(savedPost.getId(), savedPost.getContent(), "POST");

        // 6. Publish Kafka Event
        try {
            PostCreatedEvent event = PostCreatedEvent.builder()
                    .postId(savedPost.getId())
                    .authorAgentId(author.getId())
                    .content(savedPost.getContent())
                    .postType(savedPost.getType().name())
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            kafkaTemplate.send("post-created", savedPost.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Kafka publish skipped: {}", e.getMessage());
        }

        // 7. Trigger Relevance Routing Engine asynchronously / immediately
        try {
            routingEngine.evaluatePost(savedPost.getId());
        } catch (Exception e) {
            log.error("Relevance routing failed for post {}: {}", savedPost.getId(), e.getMessage());
        }

        return mapToResponse(savedPost, null);
    }

    public PostResponse getPostById(UUID id, UUID viewerAgentId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        return mapToResponse(post, viewerAgentId);
    }

    public Page<PostResponse> getFeed(UUID viewerAgentId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return posts.map(p -> mapToResponse(p, viewerAgentId));
    }

    public Page<PostResponse> searchPosts(String query, Pageable pageable) {
        Page<Post> posts = postRepository.searchByContent(query, pageable);
        return posts.map(p -> mapToResponse(p, null));
    }

    @Transactional
    public void deletePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        post.setStatus(PostStatus.DELETED);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public PostResponse mapToResponse(Post post, UUID viewerAgentId) {
        List<PostTopic> topics = postTopicRepository.findByPostId(post.getId());
        List<PostEntity> entities = postEntityRepository.findByPostId(post.getId());
        long commentsCount = commentRepository.countByPostId(post.getId());

        List<String> topicStrings = topics.stream().map(PostTopic::getTopic).collect(Collectors.toList());
        List<String> entityStrings = entities.stream().map(e -> e.getEntityName() + " (" + e.getEntityType() + ")").collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .authorAgentId(post.getAuthorAgent().getId())
                .authorAgentName(post.getAuthorAgent().getName())
                .authorCompanyName(post.getAuthorAgent().getCompany() != null ? post.getAuthorAgent().getCompany().getName() : "Unknown")
                .authorCompanyLogo(post.getAuthorAgent().getCompany() != null ? post.getAuthorAgent().getCompany().getLogoUrl() : null)
                .content(post.getContent())
                .type(post.getType())
                .status(post.getStatus())
                .topics(topicStrings)
                .entities(entityStrings)
                .intent(post.getIntent())
                .geography(post.getGeography())
                .trustScore(post.getTrustScore())
                .commentCount(commentsCount)
                .createdAt(post.getCreatedAt())
                .build();
    }
}

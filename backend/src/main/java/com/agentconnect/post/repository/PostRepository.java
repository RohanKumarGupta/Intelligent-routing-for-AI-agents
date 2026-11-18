package com.agentconnect.post.repository;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.model.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByAuthorAgent(Agent authorAgent);

    Page<Post> findByAuthorAgentOrderByCreatedAtDesc(Agent authorAgent, Pageable pageable);

    List<Post> findByStatus(PostStatus status);

    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE status = 'PUBLISHED' ORDER BY embedding <=> cast(:embedding as vector) LIMIT :limit", nativeQuery = true)
    List<Post> findByEmbeddingNear(@Param("embedding") String embedding, @Param("limit") int limit);

    Optional<Post> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchByContent(@Param("query") String query, Pageable pageable);
}

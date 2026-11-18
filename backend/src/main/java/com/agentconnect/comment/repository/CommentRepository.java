package com.agentconnect.comment.repository;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);
    List<Comment> findByPostIdOrderByCreatedAtAsc(UUID postId);
    List<Comment> findByAuthorAgent(Agent authorAgent);
    long countByPostId(UUID postId);
}

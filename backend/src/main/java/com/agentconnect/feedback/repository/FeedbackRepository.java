package com.agentconnect.feedback.repository;

import com.agentconnect.feedback.model.Feedback;
import com.agentconnect.feedback.model.FeedbackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByAgentId(UUID agentId);
    List<Feedback> findByPostId(UUID postId);
    Page<Feedback> findByAgentId(UUID agentId, Pageable pageable);
    long countByType(FeedbackType type);
    long countByAgentIdAndType(UUID agentId, FeedbackType type);
}

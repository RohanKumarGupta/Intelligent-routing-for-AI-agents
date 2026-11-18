package com.agentconnect.routing.repository;

import com.agentconnect.routing.model.RoutingDecision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoutingDecisionRepository extends JpaRepository<RoutingDecision, UUID> {
    List<RoutingDecision> findByPostId(UUID postId);
    Page<RoutingDecision> findByAgentId(UUID agentId, Pageable pageable);
    List<RoutingDecision> findByPostIdOrderByFinalScoreDesc(UUID postId);
}

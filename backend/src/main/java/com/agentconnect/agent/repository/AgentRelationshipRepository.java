package com.agentconnect.agent.repository;

import com.agentconnect.agent.model.AgentRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRelationshipRepository extends JpaRepository<AgentRelationship, UUID> {
    List<AgentRelationship> findByAgentId(UUID agentId);
    Optional<AgentRelationship> findByAgentIdAndRelatedAgentId(UUID agentId, UUID relatedAgentId);
}

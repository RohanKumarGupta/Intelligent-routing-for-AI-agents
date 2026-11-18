package com.agentconnect.feedback.repository;

import com.agentconnect.feedback.model.AgentReputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentReputationRepository extends JpaRepository<AgentReputation, UUID> {
    Optional<AgentReputation> findByAgentId(UUID agentId);
}

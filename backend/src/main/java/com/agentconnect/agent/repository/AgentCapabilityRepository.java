package com.agentconnect.agent.repository;

import com.agentconnect.agent.model.AgentCapability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgentCapabilityRepository extends JpaRepository<AgentCapability, UUID> {
    List<AgentCapability> findByAgentId(UUID agentId);
    List<AgentCapability> findByCapabilityIn(Collection<String> capabilities);
}

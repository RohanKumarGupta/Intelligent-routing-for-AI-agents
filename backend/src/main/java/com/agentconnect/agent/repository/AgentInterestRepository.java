package com.agentconnect.agent.repository;

import com.agentconnect.agent.model.AgentInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgentInterestRepository extends JpaRepository<AgentInterest, UUID> {
    List<AgentInterest> findByAgentId(UUID agentId);
    List<AgentInterest> findByTopic(String topic);
    List<AgentInterest> findByTopicIn(Collection<String> topics);
}

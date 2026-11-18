package com.agentconnect.agent.repository;

import com.agentconnect.agent.model.AgentSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgentSubscriptionRepository extends JpaRepository<AgentSubscription, UUID> {
    List<AgentSubscription> findByAgentId(UUID agentId);
    List<AgentSubscription> findByAgentIdAndActive(UUID agentId, boolean active);
    List<AgentSubscription> findByTopic(String topic);
    List<AgentSubscription> findByTopicInAndActiveTrue(Collection<String> topics);
}

package com.agentconnect.agent.repository;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.model.AgentStatus;
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
public interface AgentRepository extends JpaRepository<Agent, UUID> {

    @Query(value = "SELECT * FROM agents ORDER BY embedding <=> cast(:embedding as vector) LIMIT :limit", nativeQuery = true)
    List<Agent> findByEmbeddingNear(@Param("embedding") String embedding, @Param("limit") int limit);

    List<Agent> findByStatus(AgentStatus status);

    Page<Agent> findByStatus(AgentStatus status, Pageable pageable);

    Optional<Agent> findByName(String name);

    @Query("SELECT a FROM Agent a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Agent> searchAgents(@Param("query") String query, Pageable pageable);

    @Query("SELECT a FROM Agent a WHERE a.company.id = :companyId")
    List<Agent> findByCompanyId(@Param("companyId") UUID companyId);
}

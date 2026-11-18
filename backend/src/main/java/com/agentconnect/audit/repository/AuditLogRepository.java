package com.agentconnect.audit.repository;

import com.agentconnect.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByAction(String action, Pageable pageable);
    Page<AuditLog> findByResourceType(String resourceType, Pageable pageable);
    Page<AuditLog> findByActorId(String actorId, Pageable pageable);
}

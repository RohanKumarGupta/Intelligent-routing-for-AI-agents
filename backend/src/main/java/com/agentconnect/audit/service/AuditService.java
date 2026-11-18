package com.agentconnect.audit.service;

import com.agentconnect.audit.model.AuditLog;
import com.agentconnect.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void logAsync(String actorId, String actorType, String action, String resourceType, String resourceId, String detailsJson, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(actorId)
                    .actorType(actorType)
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .detailsJson(detailsJson)
                    .ipAddress(ipAddress)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Failed to write audit log: {}", e.getMessage());
        }
    }

    public void log(String actorId, String actorType, String action, String resourceType, String resourceId, String detailsJson) {
        logAsync(actorId, actorType, action, resourceType, resourceId, detailsJson, null);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}

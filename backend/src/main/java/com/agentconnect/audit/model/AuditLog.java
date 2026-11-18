package com.agentconnect.audit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String actorId;

    private String actorType;

    @Column(nullable = false)
    private String action;

    private String resourceType;

    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    private String ipAddress;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

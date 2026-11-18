package com.agentconnect.notification.repository;

import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByTargetAgentId(UUID targetAgentId);
    Page<Notification> findByTargetAgentIdOrderByCreatedAtDesc(UUID targetAgentId, Pageable pageable);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByPostId(UUID postId);
    long countByStatus(NotificationStatus status);
}

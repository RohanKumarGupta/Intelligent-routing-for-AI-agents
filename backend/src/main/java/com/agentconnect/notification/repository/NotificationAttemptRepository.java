package com.agentconnect.notification.repository;

import com.agentconnect.notification.model.NotificationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationAttemptRepository extends JpaRepository<NotificationAttempt, UUID> {
    List<NotificationAttempt> findByNotificationIdOrderByAttemptNumberAsc(UUID notificationId);
}

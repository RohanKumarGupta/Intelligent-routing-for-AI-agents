package com.agentconnect.moderation.repository;

import com.agentconnect.moderation.model.ModerationResult;
import com.agentconnect.moderation.model.ModerationResultType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModerationResultRepository extends JpaRepository<ModerationResult, UUID> {
    Optional<ModerationResult> findByContentIdAndContentType(UUID contentId, String contentType);
    Page<ModerationResult> findByResult(ModerationResultType result, Pageable pageable);
}

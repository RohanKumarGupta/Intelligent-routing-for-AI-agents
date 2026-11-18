-- =============================================================================
-- V5: Create notifications and notification_attempts tables
-- =============================================================================

CREATE TABLE notifications (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id             UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    target_agent_id     UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    relevance_score     NUMERIC(4,2) NOT NULL,
    reason_json         TEXT,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    attempt_count       INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    delivered_at        TIMESTAMPTZ
);

CREATE INDEX idx_notifications_post_id ON notifications(post_id);
CREATE INDEX idx_notifications_target_agent_id ON notifications(target_agent_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);

CREATE TABLE notification_attempts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id     UUID NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    attempt_number      INTEGER NOT NULL,
    status              VARCHAR(50) NOT NULL,
    response_code       INTEGER,
    error_message       TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_attempts_notification_id ON notification_attempts(notification_id);

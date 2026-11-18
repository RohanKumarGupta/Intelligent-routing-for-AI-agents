-- =============================================================================
-- V6: Create feedback and agent_reputations tables
-- =============================================================================

CREATE TABLE feedback (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id            UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    post_id             UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    notification_id     UUID REFERENCES notifications(id) ON DELETE SET NULL,
    type                VARCHAR(50) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feedback_agent_id ON feedback(agent_id);
CREATE INDEX idx_feedback_post_id ON feedback(post_id);
CREATE INDEX idx_feedback_type ON feedback(type);

CREATE TABLE agent_reputations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id            UUID NOT NULL UNIQUE REFERENCES agents(id) ON DELETE CASCADE,
    trust_score         NUMERIC(4,2) NOT NULL DEFAULT 0.50,
    total_posts         INTEGER NOT NULL DEFAULT 0,
    successful_posts    INTEGER NOT NULL DEFAULT 0,
    spam_count          INTEGER NOT NULL DEFAULT 0,
    policy_violations   INTEGER NOT NULL DEFAULT 0,
    reports_received    INTEGER NOT NULL DEFAULT 0,
    last_calculated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_agent_reputations_trust_score ON agent_reputations(trust_score);

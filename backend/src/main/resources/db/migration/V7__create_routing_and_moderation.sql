-- =============================================================================
-- V7: Create routing_decisions, routing_configurations, and moderation_results
-- =============================================================================

CREATE TABLE routing_decisions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id             UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    agent_id            UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    semantic_score      NUMERIC(5,4) NOT NULL DEFAULT 0,
    topic_score         NUMERIC(5,4) NOT NULL DEFAULT 0,
    subscription_score  NUMERIC(5,4) NOT NULL DEFAULT 0,
    capability_score    NUMERIC(5,4) NOT NULL DEFAULT 0,
    relationship_score  NUMERIC(5,4) NOT NULL DEFAULT 0,
    geography_score     NUMERIC(5,4) NOT NULL DEFAULT 0,
    historical_score    NUMERIC(5,4) NOT NULL DEFAULT 0,
    final_score         NUMERIC(5,4) NOT NULL DEFAULT 0,
    decision            VARCHAR(20) NOT NULL,
    reasons_json        TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_routing_decisions_post_id ON routing_decisions(post_id);
CREATE INDEX idx_routing_decisions_agent_id ON routing_decisions(agent_id);
CREATE INDEX idx_routing_decisions_decision ON routing_decisions(decision);
CREATE INDEX idx_routing_decisions_final_score ON routing_decisions(final_score DESC);

CREATE TABLE routing_configurations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(100) NOT NULL,
    semantic_weight     NUMERIC(4,3) NOT NULL DEFAULT 0.350,
    topic_weight        NUMERIC(4,3) NOT NULL DEFAULT 0.200,
    subscription_weight NUMERIC(4,3) NOT NULL DEFAULT 0.150,
    capability_weight   NUMERIC(4,3) NOT NULL DEFAULT 0.100,
    relationship_weight NUMERIC(4,3) NOT NULL DEFAULT 0.100,
    geography_weight    NUMERIC(4,3) NOT NULL DEFAULT 0.050,
    historical_weight   NUMERIC(4,3) NOT NULL DEFAULT 0.050,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE moderation_results (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id          UUID NOT NULL,
    content_type        VARCHAR(50) NOT NULL,
    spam_score          NUMERIC(4,3) NOT NULL DEFAULT 0.0,
    toxicity_score      NUMERIC(4,3) NOT NULL DEFAULT 0.0,
    policy_violation    BOOLEAN NOT NULL DEFAULT FALSE,
    result              VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    details_json        TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_moderation_results_content ON moderation_results(content_id, content_type);
CREATE INDEX idx_moderation_results_result ON moderation_results(result);

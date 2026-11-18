-- =============================================================================
-- V2: Create agent metadata tables (interests, capabilities, subscriptions, relationships)
-- =============================================================================

CREATE TABLE agent_interests (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id    UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    topic       VARCHAR(255) NOT NULL,
    weight      NUMERIC(4,2) NOT NULL DEFAULT 1.0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_agent_topic UNIQUE (agent_id, topic)
);

CREATE INDEX idx_agent_interests_agent_id ON agent_interests(agent_id);
CREATE INDEX idx_agent_interests_topic ON agent_interests(topic);

CREATE TABLE agent_capabilities (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id    UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    capability  VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_agent_capability UNIQUE (agent_id, capability)
);

CREATE INDEX idx_agent_capabilities_agent_id ON agent_capabilities(agent_id);
CREATE INDEX idx_agent_capabilities_capability ON agent_capabilities(capability);

CREATE TABLE agent_subscriptions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id    UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    topic       VARCHAR(255) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_agent_subscription UNIQUE (agent_id, topic)
);

CREATE INDEX idx_agent_subscriptions_agent_id ON agent_subscriptions(agent_id);
CREATE INDEX idx_agent_subscriptions_topic ON agent_subscriptions(topic);

CREATE TABLE agent_relationships (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id            UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    related_agent_id    UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    relationship_type   VARCHAR(100) NOT NULL,
    strength            NUMERIC(4,2) NOT NULL DEFAULT 0.5,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_agent_relationship UNIQUE (agent_id, related_agent_id)
);

CREATE INDEX idx_agent_relationships_agent_id ON agent_relationships(agent_id);
CREATE INDEX idx_agent_relationships_related_agent ON agent_relationships(related_agent_id);

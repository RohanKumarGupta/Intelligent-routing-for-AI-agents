-- =============================================================================
-- V3: Create posts, post_topics, and post_entities tables
-- =============================================================================

CREATE TABLE posts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_agent_id     UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    content             TEXT NOT NULL,
    type                VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    status              VARCHAR(50) NOT NULL DEFAULT 'PUBLISHED',
    topics_json         JSONB,
    entities_json       JSONB,
    intent              VARCHAR(100),
    geography           VARCHAR(10),
    embedding           vector(768),
    trust_score         NUMERIC(4,2) NOT NULL DEFAULT 0.50,
    expires_at          TIMESTAMPTZ,
    idempotency_key     VARCHAR(255) UNIQUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_author_agent_id ON posts(author_agent_id);
CREATE INDEX idx_posts_type ON posts(type);
CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_intent ON posts(intent);
CREATE INDEX idx_posts_geography ON posts(geography);

CREATE TABLE post_topics (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id     UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    topic       VARCHAR(255) NOT NULL,
    confidence  NUMERIC(4,2) NOT NULL DEFAULT 1.0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_post_topics_post_id ON post_topics(post_id);
CREATE INDEX idx_post_topics_topic ON post_topics(topic);

CREATE TABLE post_entities (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id     UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    entity_name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    confidence  NUMERIC(4,2) NOT NULL DEFAULT 1.0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_post_entities_post_id ON post_entities(post_id);
CREATE INDEX idx_post_entities_entity_name ON post_entities(entity_name);

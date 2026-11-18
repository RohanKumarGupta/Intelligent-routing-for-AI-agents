-- =============================================================================
-- V8: Create audit_logs table and pgvector HNSW indexes
-- =============================================================================

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id        VARCHAR(255),
    actor_type      VARCHAR(50),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(100),
    resource_id     VARCHAR(255),
    details_json    TEXT,
    ip_address      VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);

-- Initial default routing configuration
INSERT INTO routing_configurations (id, name, semantic_weight, topic_weight, subscription_weight, capability_weight, relationship_weight, geography_weight, historical_weight, active)
VALUES (
    gen_random_uuid(),
    'Default Production Weights',
    0.350,
    0.200,
    0.150,
    0.100,
    0.100,
    0.050,
    0.050,
    TRUE
);

-- pgvector indexes for high-performance cosine similarity search
CREATE INDEX IF NOT EXISTS idx_agents_embedding_hnsw ON agents USING hnsw (embedding vector_cosine_ops);
CREATE INDEX IF NOT EXISTS idx_posts_embedding_hnsw ON posts USING hnsw (embedding vector_cosine_ops);

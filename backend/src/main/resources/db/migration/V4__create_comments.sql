-- =============================================================================
-- V4: Create comments table
-- =============================================================================

CREATE TABLE comments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id             UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_agent_id     UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    parent_comment_id   UUID REFERENCES comments(id) ON DELETE CASCADE,
    content             TEXT NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PUBLISHED',
    generated_by_ai     BOOLEAN NOT NULL DEFAULT FALSE,
    moderation_status   VARCHAR(50) NOT NULL DEFAULT 'APPROVED',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_author_agent_id ON comments(author_agent_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);

-- =============================================================================
-- V1: Create companies, users, and agents tables
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE companies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL UNIQUE,
    description     TEXT,
    website         VARCHAR(500),
    logo_url        VARCHAR(500),
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    company_id      UUID REFERENCES companies(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE agents (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id              UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name                    VARCHAR(255) NOT NULL,
    description             TEXT,
    status                  VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',
    webhook_url             VARCHAR(500),
    api_key_hash            VARCHAR(255),
    public_key              TEXT,
    notification_threshold  NUMERIC(4,2) NOT NULL DEFAULT 0.75,
    rate_limit              INTEGER NOT NULL DEFAULT 100,
    embedding               vector(768),
    trust_score             NUMERIC(4,2) NOT NULL DEFAULT 0.50,
    verified                BOOLEAN NOT NULL DEFAULT FALSE,
    spam_count              INTEGER NOT NULL DEFAULT 0,
    policy_violations       INTEGER NOT NULL DEFAULT 0,
    successful_posts        INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_company_agent_name UNIQUE (company_id, name)
);

CREATE INDEX idx_agents_company_id ON agents(company_id);
CREATE INDEX idx_agents_status ON agents(status);
CREATE INDEX idx_agents_verified ON agents(verified);

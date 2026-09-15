CREATE SCHEMA IF NOT EXISTS evidence;

CREATE TABLE integration.github_account (
    account_id        bigint PRIMARY KEY,
    login             varchar(255) NOT NULL,
    account_type      varchar(32) NOT NULL,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT github_account_type_check CHECK (account_type IN ('USER', 'ORGANIZATION'))
);

CREATE TABLE integration.github_installation (
    installation_id  bigint PRIMARY KEY,
    account_id        bigint NOT NULL REFERENCES integration.github_account(account_id),
    status            varchar(32) NOT NULL DEFAULT 'ACTIVE',
    installed_at      timestamptz NOT NULL,
    suspended_at      timestamptz,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT github_installation_status_check CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE evidence.repository (
    repository_id     bigint PRIMARY KEY,
    installation_id  bigint NOT NULL REFERENCES integration.github_installation(installation_id),
    account_id        bigint NOT NULL REFERENCES integration.github_account(account_id),
    name               varchar(255) NOT NULL,
    full_name          varchar(512) NOT NULL,
    default_branch     varchar(255),
    private            boolean NOT NULL,
    archived           boolean NOT NULL DEFAULT false,
    selected_for_sync  boolean NOT NULL DEFAULT true,
    created_at         timestamptz NOT NULL DEFAULT now(),
    updated_at         timestamptz NOT NULL DEFAULT now(),
    UNIQUE (account_id, name),
    UNIQUE (installation_id, repository_id)
);

ALTER TABLE integration.webhook_delivery
    ADD COLUMN installation_id bigint;

CREATE INDEX webhook_delivery_installation_idx
    ON integration.webhook_delivery (installation_id, received_at DESC);

CREATE INDEX repository_installation_idx
    ON evidence.repository (installation_id, selected_for_sync);

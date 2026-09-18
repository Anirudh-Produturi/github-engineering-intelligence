CREATE SCHEMA IF NOT EXISTS analytics;

CREATE TABLE analytics.measurement_period (
    period_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    label varchar(128) NOT NULL,
    period_kind varchar(16) NOT NULL CHECK (period_kind IN ('BASELINE', 'CURRENT')),
    starts_on date NOT NULL,
    ends_on date NOT NULL,
    CHECK (ends_on >= starts_on),
    UNIQUE (repository_id, period_kind)
);

CREATE TABLE analytics.metric_observation (
    period_id bigint NOT NULL REFERENCES analytics.measurement_period(period_id) ON DELETE CASCADE,
    metric_key varchar(64) NOT NULL,
    display_name varchar(128) NOT NULL,
    category varchar(16) NOT NULL CHECK (category IN ('SPEED', 'EFFORT', 'QUALITY', 'FLOW')),
    metric_value numeric(14,4) NOT NULL,
    unit varchar(32) NOT NULL,
    improvement_direction varchar(8) NOT NULL CHECK (improvement_direction IN ('HIGHER', 'LOWER')),
    PRIMARY KEY (period_id, metric_key)
);

CREATE TABLE analytics.ai_adoption_snapshot (
    period_id bigint NOT NULL REFERENCES analytics.measurement_period(period_id) ON DELETE CASCADE,
    capability varchar(32) NOT NULL CHECK (capability IN ('CODING', 'TESTING', 'PR_REVIEW', 'DOCUMENTATION')),
    adoption_percent numeric(5,2) NOT NULL CHECK (adoption_percent BETWEEN 0 AND 100),
    evidence_method varchar(64) NOT NULL,
    PRIMARY KEY (period_id, capability)
);

CREATE TABLE analytics.ai_intervention (
    intervention_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    capability varchar(32) NOT NULL,
    title varchar(255) NOT NULL,
    started_on date NOT NULL,
    ended_on date,
    status varchar(16) NOT NULL CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED'))
);

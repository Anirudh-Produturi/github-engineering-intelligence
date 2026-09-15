CREATE TABLE evidence.contributor (
    contributor_id bigint PRIMARY KEY,
    account_id bigint NOT NULL REFERENCES integration.github_account(account_id),
    login varchar(255) NOT NULL,
    UNIQUE (account_id, login)
);

CREATE TABLE evidence.pull_request (
    pull_request_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    number integer NOT NULL,
    author_id bigint REFERENCES evidence.contributor(contributor_id),
    state varchar(16) NOT NULL CHECK (state IN ('OPEN', 'CLOSED', 'MERGED')),
    opened_at timestamptz NOT NULL,
    ready_for_review_at timestamptz,
    first_review_requested_at timestamptz,
    merged_at timestamptz,
    closed_at timestamptz,
    additions integer NOT NULL DEFAULT 0 CHECK (additions >= 0),
    deletions integer NOT NULL DEFAULT 0 CHECK (deletions >= 0),
    changed_files integer NOT NULL DEFAULT 0 CHECK (changed_files >= 0),
    draft boolean NOT NULL DEFAULT false,
    UNIQUE (repository_id, number)
);

CREATE TABLE evidence.pull_request_review (
    review_id bigint PRIMARY KEY,
    pull_request_id bigint NOT NULL REFERENCES evidence.pull_request(pull_request_id) ON DELETE CASCADE,
    reviewer_id bigint REFERENCES evidence.contributor(contributor_id),
    state varchar(32) NOT NULL,
    submitted_at timestamptz NOT NULL
);

CREATE TABLE evidence.review_request (
    pull_request_id bigint NOT NULL REFERENCES evidence.pull_request(pull_request_id) ON DELETE CASCADE,
    reviewer_id bigint NOT NULL REFERENCES evidence.contributor(contributor_id),
    requested_at timestamptz NOT NULL,
    fulfilled_at timestamptz,
    PRIMARY KEY (pull_request_id, reviewer_id, requested_at)
);

CREATE TABLE evidence.file_change (
    pull_request_id bigint NOT NULL REFERENCES evidence.pull_request(pull_request_id) ON DELETE CASCADE,
    path text NOT NULL,
    additions integer NOT NULL DEFAULT 0,
    deletions integer NOT NULL DEFAULT 0,
    PRIMARY KEY (pull_request_id, path)
);

CREATE TABLE evidence.issue (
    issue_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    number integer NOT NULL,
    state varchar(16) NOT NULL CHECK (state IN ('OPEN', 'CLOSED')),
    created_at timestamptz NOT NULL,
    closed_at timestamptz,
    is_bug boolean NOT NULL DEFAULT false,
    UNIQUE (repository_id, number)
);

CREATE TABLE evidence.bug_file_link (
    issue_id bigint NOT NULL REFERENCES evidence.issue(issue_id) ON DELETE CASCADE,
    path text NOT NULL,
    linkage_method varchar(32) NOT NULL,
    PRIMARY KEY (issue_id, path)
);

CREATE TABLE evidence.workflow_job (
    workflow_job_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    workflow_name varchar(255) NOT NULL,
    job_name varchar(255) NOT NULL,
    head_sha varchar(64) NOT NULL,
    attempt integer NOT NULL DEFAULT 1 CHECK (attempt > 0),
    started_at timestamptz NOT NULL,
    completed_at timestamptz,
    conclusion varchar(32)
);

CREATE TABLE evidence.release (
    release_id bigint PRIMARY KEY,
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    tag_name varchar(255) NOT NULL,
    published_at timestamptz NOT NULL,
    prerelease boolean NOT NULL DEFAULT false,
    UNIQUE (repository_id, tag_name)
);

CREATE TABLE evidence.coverage_snapshot (
    repository_id bigint NOT NULL REFERENCES evidence.repository(repository_id) ON DELETE CASCADE,
    path text NOT NULL,
    format varchar(32) NOT NULL CHECK (format IN ('JACOCO', 'COBERTURA', 'LCOV')),
    line_coverage numeric(5,4) NOT NULL CHECK (line_coverage BETWEEN 0 AND 1),
    captured_at timestamptz NOT NULL,
    PRIMARY KEY (repository_id, path, captured_at)
);

CREATE INDEX pull_request_repository_time_idx ON evidence.pull_request(repository_id, opened_at DESC);
CREATE INDEX review_pull_request_time_idx ON evidence.pull_request_review(pull_request_id, submitted_at);
CREATE INDEX file_change_path_idx ON evidence.file_change(path);
CREATE INDEX issue_repository_age_idx ON evidence.issue(repository_id, state, created_at);
CREATE INDEX workflow_job_flake_idx ON evidence.workflow_job(repository_id, workflow_name, job_name, head_sha, attempt);
CREATE INDEX release_repository_time_idx ON evidence.release(repository_id, published_at DESC);
CREATE INDEX coverage_latest_idx ON evidence.coverage_snapshot(repository_id, captured_at DESC);

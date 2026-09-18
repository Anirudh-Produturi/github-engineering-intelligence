# ADR 0001: Modular API with a separate ingestion worker

- Status: accepted for MVP
- Date: 2026-09-14
- Amended: 2026-09-18 by [ADR 0003](0003-server-rendered-ai-impact-slice.md)

## Decision

Use a Java 21 Spring Boot modular API for secure ingress and query operations, PostgreSQL for durable evidence, and reserve a separately deployable Python worker for future high-volume GitHub backfills and asynchronous metric computation.

The current vertical slice deliberately computes dashboard metrics in Java. UI and JSON API queries share the same application service, preventing metric drift while the product is small. The Python worker remains an architectural extension point, not a deployed dependency.

The webhook endpoint writes an authenticated delivery to a transactional inbox before acknowledging it. Workers may process a delivery more than once, so all normalized writes must be idempotent.

## Consequences

This keeps the first deployment operationally simple while retaining a clear extraction path when ingestion volume justifies another runtime. When a worker is introduced, schemas and contracts must be versioned and tested, and services must not share in-memory state.

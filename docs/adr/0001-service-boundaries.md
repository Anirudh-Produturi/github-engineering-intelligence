# ADR 0001: Modular API with a separate ingestion worker

- Status: accepted for MVP
- Date: 2026-09-14

## Decision

Use a Java 21 Spring Boot modular API for secure ingress and query operations, PostgreSQL for durable evidence, and a separately deployable Python worker for GitHub backfills and metric computation.

The webhook endpoint writes an authenticated delivery to a transactional inbox before acknowledging it. Workers may process a delivery more than once, so all normalized writes must be idempotent.

## Consequences

This establishes clear security and analytics boundaries and demonstrates both enterprise API and data-engineering practices. It adds a second runtime, so schemas and contracts must be versioned and tested. Services do not share in-memory state.


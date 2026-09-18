# Ingestion and metrics worker

Status: reserved; it is not required to run the current application.

This boundary may later consume durable inbox records, backfill GitHub data with installation tokens, normalize evidence, and calculate versioned metric facts when organization-scale volume justifies a separate runtime. The current vertical slice computes synthetic-demo metrics in the Java service so the UI and JSON API use one calculation path.

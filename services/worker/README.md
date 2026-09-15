# Ingestion and metrics worker

This boundary will consume durable inbox records, backfill GitHub data with installation tokens, normalize evidence, and calculate versioned metric facts. It intentionally has no implementation until the inbox contract is proven by the first vertical slice.


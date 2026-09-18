# ADR 0003: Server-rendered UI and explainable AI-impact slice

- Status: accepted
- Date: 2026-09-18

## Context

The original product brief proposed a separate React frontend and broad engineering-intelligence roadmap. The selected portfolio and hackathon slice needs a working, credential-free demo that connects team-level AI adoption with observable engineering outcomes while avoiding an enterprise efficiency grade.

## Decision

Use Spring MVC and Thymeleaf for the current dashboard. The HTML view and JSON endpoint consume the same `DashboardMetrics` query model.

Implement AI opportunity matching as deterministic Java rules over aggregate measurement periods and adoption snapshots. Recommendations must expose their rationale, propose a bounded experiment, include confidence, and state that before/after correlation does not prove causation.

Keep the future worker boundary reserved, but do not require Python or a JavaScript frontend to run the current product.

## Consequences

- One deployable API artifact serves HTML and JSON, reducing local setup and contract drift.
- The demo works without Node.js, GitHub credentials, or an external identity provider.
- Opportunity decisions are testable and explainable.
- Interactive client-side exploration is limited compared with a full SPA.
- Real adoption ingestion, live GitHub normalization, and controlled-experiment design remain separate future capabilities.

## Guardrails

- No individual adoption or productivity ranking.
- No automated code-review approval recommendation.
- No causal claim from an observational before/after comparison.
- No single composite enterprise efficiency score.
- Organization deployment requires tenant authorization and security review beyond the current OIDC boundary.

# Implementation status

Last updated: 2026-09-18

Legend: **Complete**, **Partial**, **Planned**.

| Capability | Status | Evidence or limitation |
|---|---|---|
| Gradle/Java 21 build | Complete | Wrapper, toolchain, CI, and Docker build. |
| PostgreSQL/Flyway | Complete | Four migrations covering integration, evidence, and analytics. |
| Personal synthetic demo | Complete | Starts without GitHub or OIDC credentials. |
| Server-rendered dashboard | Complete | Spring MVC, Thymeleaf, and responsive CSS. |
| JSON metrics API | Complete | Shared model with dashboard. |
| PR cycle and review wait | Complete for synthetic evidence | Live normalization is planned. |
| Changed-file and PR risk signals | Complete for synthetic evidence | Risk is explainable and not a quality score. |
| Coverage analysis | Partial | Three parsers implemented; upload/CI ingestion is planned. |
| Flaky CI candidates | Complete for synthetic evidence | Live workflow ingestion is planned. |
| Issue aging and releases | Complete for synthetic evidence | Live ingestion is planned. |
| Bug-associated files | Complete for synthetic evidence | Depends on real label/link conventions later. |
| Workload concentration | Complete | Cohort suppression implemented; no individuals exposed. |
| AI adoption snapshots | Complete for synthetic survey data | Real aggregate evidence is planned. |
| Before/after comparisons | Complete | Measurement periods and improvement direction implemented. |
| Explainable opportunity matching | Complete | Deterministic, tested rules. |
| GitHub webhook authentication | Complete | HMAC fixture and constant-time comparison. |
| Durable webhook inbox | Complete | Delivery-ID idempotency. |
| Webhook normalization | Planned | Inbox events are not yet transformed into evidence. |
| GitHub App authentication/backfill | Planned | Requires later credentials and administrator approval. |
| Organization OIDC enforcement | Partial | JWT boundary exists; tenant query authorization/RBAC remain. |
| Team/portfolio/enterprise views | Planned | Current view is repository-level. |
| Deployment/reliability metrics | Planned | Requires production-environment and incident evidence. |
| Production observability | Partial | Actuator/Prometheus available; dashboards, alerts, and SLOs remain. |
| Production deployment | Planned | Target platform is not selected. |

## Verified test state

The most recent full run with Docker completed 15 tests with zero failures and zero skips. The suite covers unit logic, PostgreSQL migrations and seeding, API responses, HTML rendering, coverage parsing, configuration validation, webhook verification, inbox idempotency, and recommendation matching.


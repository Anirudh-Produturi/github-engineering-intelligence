# ADR 0002: One codebase with personal and organization modes

- Status: accepted
- Date: 2026-09-14

## Decision

Maintain one application and tenant-aware data model. `personal` mode is the local portfolio experience: synthetic demo data, local PostgreSQL, and no external identity provider. `organization` mode disables demo seeding, requires a non-development webhook secret, raises the minimum cohort default, and authenticates non-webhook APIs using OIDC JWTs.

GitHub installation, account, and repository identifiers remain explicit in both modes. Personal mode is therefore a one-tenant deployment of the same model rather than a separate edition.

## Guardrails

- Organization mode fails startup when demo data, OIDC, cohort size, or webhook-secret configuration is unsafe.
- Webhook endpoints remain unauthenticated at the HTTP identity layer because GitHub calls them; exact-body HMAC verification authenticates deliveries.
- Raw inbox rows carry the installation ID when supplied. They do not use a foreign key because an installation lifecycle event can legitimately arrive before its normalized installation record exists.
- Tenant-aware query authorization and role mapping must be completed before organization production use.

## Consequences

The personal demo stays easy to run while migrations, ingestion, and metrics are built against organization-safe ownership boundaries. Organization adoption still requires security review, approved identity configuration, managed secrets, and production infrastructure.


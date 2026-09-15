# GitHub Engineering Intelligence

An organization-level engineering delivery and reliability dashboard built around transparent metric definitions and responsible-use safeguards.

## Current vertical slice

- Java 21/Spring Boot API
- GitHub webhook HMAC-SHA256 verification
- Durable, idempotent webhook inbox in PostgreSQL
- Flyway-managed schema
- Unit and PostgreSQL integration tests
- Docker Compose development environment
- Python worker boundary reserved for ingestion and metric computation
- Server-rendered Spring/Thymeleaf engineering metrics dashboard
- Synthetic PR, review, file, issue, CI, release, coverage, and bug-link evidence
- JSON metric API backed by the same calculations as the UI

No individual productivity scores or source-code contents are collected.

## Run locally

Requirements: Docker with Compose.

```bash
cp .env.example .env
docker compose up --build
```

The API health endpoint is `http://localhost:8080/actuator/health`.

Open the personal demo dashboard at `http://localhost:8080/`. Its synthetic dataset demonstrates PR cycle time, review wait time, changed-file frequency, coverage risk, flaky CI candidates, PR size/risk, issue aging, release frequency, bug hotspots, and privacy-protected team workload concentration.

Personal mode is the default. It seeds a clearly synthetic organization and repository and permits local API access without configuring an identity provider. Check the active behavior at `GET /api/v1/system/mode`.

## Organization mode

Organization mode uses the same application and tenant-aware schema, but disables demo data and requires OIDC bearer-token authentication for dashboard APIs. Webhooks remain publicly reachable and are protected by GitHub HMAC verification.

Use `deploy/organization.env.example` as a configuration contract, not as a production secrets file. Set both `SPRING_PROFILES_ACTIVE=organization` and `APP_MODE=organization`; startup fails when OIDC, cohort, demo-data, or webhook-secret safeguards are invalid. A production deployment must inject secrets from the organization's approved secret manager.

GitHub webhook requests are accepted at `POST /api/v1/github/webhooks`. Include `X-GitHub-Delivery`, `X-GitHub-Event`, and `X-Hub-Signature-256` headers. The signature is an HMAC-SHA256 over the exact body using `GITHUB_WEBHOOK_SECRET`.

## Test

```bash
cd services/api
./gradlew test
```

The checked-in Gradle Wrapper provides a consistent build without requiring a system Gradle installation. The integration test uses Testcontainers and therefore needs Docker.

Coverage parsing supports JaCoCo XML, Cobertura XML, and LCOV. Live report upload and GitHub historical backfill remain future ingestion work; the current dashboard uses synthetic evidence so it runs without credentials.


## Documentation

- [MVP product and architecture](outputs/mvp-product-architecture.md)
- [Architecture decision: modular monolith and worker](docs/adr/0001-service-boundaries.md)
- [Architecture decision: dual-mode deployment](docs/adr/0002-dual-mode-deployment.md)
- [Responsible-use policy](docs/responsible-use.md)

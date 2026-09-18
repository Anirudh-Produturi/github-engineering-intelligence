# API reference

Last verified: 2026-09-18

The personal demo permits local requests without authentication. Organization mode requires an OIDC bearer token except for health and GitHub webhook ingress.

## Dashboard

### `GET /`

Renders the Spring/Thymeleaf dashboard for the first repository selected for synchronization. If no repository exists, it renders the getting-started page.

Response: HTML.

## Repository metrics

### `GET /api/v1/repositories/{repositoryId}/metrics`

Returns the same metric model used by the HTML dashboard.

The response contains:

- Repository identity and delivery summary.
- Baseline/current outcome comparisons.
- Team-level AI adoption.
- Explainable AI opportunities.
- Frequently changed files.
- Low-coverage paths.
- PR size/risk signals.
- Issue age bands.
- Flaky CI candidates.
- Bug-associated paths.
- Privacy-protected workload concentration.
- Evidence-quality context.

Personal demo example:

```bash
curl http://localhost:8080/api/v1/repositories/9000001/metrics
```

`9000001` is reserved for the synthetic demo repository and is not a production identifier.

## Runtime mode

### `GET /api/v1/system/mode`

Returns the active application mode, whether demo seeding is enabled, and the minimum cohort size.

Example:

```json
{
  "mode": "personal",
  "demoData": true,
  "minimumCohortSize": 3
}
```

## GitHub webhook ingress

### `POST /api/v1/github/webhooks`

Required headers:

- `X-GitHub-Delivery`: GitHub delivery identifier used for idempotency.
- `X-GitHub-Event`: event type.
- `X-Hub-Signature-256`: `sha256=` HMAC signature over the exact request bytes.

Responses:

| Status | Body status | Meaning |
|---|---|---|
| 202 | `accepted` | Valid delivery stored for the first time. |
| 200 | `duplicate` | Delivery ID was already stored. |
| 400 | `invalid_json` | Body is not valid JSON. |
| 400 | `missing_installation` | Organization mode requires an installation ID. |
| 401 | `invalid_signature` | Signature is missing or invalid. |

The endpoint currently stores authenticated deliveries in the durable inbox. Normalization into engineering evidence is future work.

## Actuator

### `GET /actuator/health`

Returns application health. This endpoint is public in both modes.

### `GET /actuator/prometheus`

Exposes Prometheus-format JVM and application metrics when permitted by the active security configuration.

## Error behavior

The current API is an MVP and does not yet provide a versioned problem-details envelope for all errors. Repository identifiers must exist and must eventually be checked against the authenticated tenant before organization production use.


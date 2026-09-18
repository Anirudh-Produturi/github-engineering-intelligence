# Demo walkthrough

This walkthrough demonstrates the product without GitHub credentials or employee data.

## Start the demo

```bash
cp .env.example .env
docker compose up --build --detach
```

Wait for health:

```bash
curl http://localhost:8080/actuator/health
```

Open `http://localhost:8080/`.

## Storyline

### 1. Establish the engineering baseline

Show the delivery summary and explain that the repository is synthetic. Highlight:

- Median PR cycle time.
- Median review wait.
- Open PRs and issues.
- Release frequency.

The product measures systems and queues, not individual productivity.

### 2. Compare before and after

Use the Engineering Outcomes panel:

| Signal | Before | After |
|---|---:|---:|
| Test coverage | 62% | 76% |
| Rework | 14% | 9% |
| PR cycle time | 52 hours | 48 hours |
| Review wait | 20 hours | 12 hours |

Positive change is normalized using each metric's declared improvement direction. Immediately point out the correlation warning: the dashboard does not claim AI caused these changes.

### 3. Understand AI adoption

Show aggregated adoption for coding, testing, PR-review, and documentation capabilities. Explain that the evidence is a synthetic team survey and that no individual usage is tracked.

### 4. Connect constraints to opportunities

Show the explainable recommendations:

- Testing adoption is low and coverage remains below the demo target, so the system proposes a testing-agent experiment.
- PR-review adoption is low and review wait remains above the demo threshold, so the system proposes AI-assisted PR preparation.

Each recommendation states why it exists, what experiment to run, and its confidence. It never automates an employment or approval decision.

### 5. Inspect supporting engineering evidence

Scroll through:

- Large/risky PR signals.
- Frequently changed files.
- Low-coverage paths.
- Issue aging.
- Flaky CI candidates.
- Files commonly involved in bugs.
- Privacy-protected review concentration.

### 6. Show API parity

Open:

```text
http://localhost:8080/api/v1/repositories/9000001/metrics
```

The HTML and JSON use the same repository query model, preventing metric-definition drift between UI and integrations.

## Stop the demo

```bash
docker compose stop
```

Use `docker compose down` to remove stopped containers and the Compose network while preserving the named database volume. Add `--volumes` only when intentionally deleting local demo data.

## Honest closing statement

The current product is a working synthetic vertical slice. Secure webhook intake, data schemas, calculations, recommendations, UI, and tests are implemented. Live GitHub backfill, event normalization, real AI-adoption ingestion, and organization authorization remain future milestones.


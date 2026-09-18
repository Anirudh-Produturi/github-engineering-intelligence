# Metric definitions — version 1

| Signal | Definition | Important limitation |
|---|---|---|
| PR cycle time | Median elapsed hours from PR opened to merged. | Calendar time; draft and working-time adjustments are not yet applied. |
| Review wait | Median elapsed hours from review request to its recorded fulfillment. | A response is only as accurate as review-request event capture. |
| Changed-file frequency | Number of ingested PR/file-change records per path; churn is additions plus deletions. | Frequent change may reflect healthy iteration, not poor design. |
| Low coverage | Latest imported file line coverage below 70%. | Coverage is not a quality score. |
| Flaky CI candidate | Same workflow job and commit has both failure and success conclusions. | Retry success is a signal, not proof of nondeterminism. |
| PR risk | Explainable heuristic from churn and changed-file count; shown for 400+ lines or 15+ files. | Must not be used to evaluate an author. |
| Issue aging | Open issues grouped into 0–7, 8–30, and 31+ day bands. | Priority and intentional backlog state are not inferred. |
| Release frequency | Non-prerelease releases in the trailing 90 days divided by three. | A GitHub release may not equal a production deployment. |
| Bug hotspot | Count of bug-labelled issues linked to fixing-PR paths. | Depends on consistent labels and linkage. |
| Workload concentration | Largest share of pending review demand, suppressed below the reviewer cohort. | Team resilience signal only; no individual rankings. |

## AI opportunity and impact demonstration

AI capability adoption is synthetic, team-level survey evidence in the personal demo. The opportunity engine uses visible rules rather than a composite efficiency score:

- Testing adoption below 50% plus coverage below 80% suggests a testing-agent pilot.
- PR-review adoption below 50% plus review wait above eight hours suggests an AI-assisted PR-preparation pilot.
- Coding adoption below 50% plus cycle time above 48 hours suggests a narrowly scoped coding-agent pilot.

Before/after change is normalized so a positive percentage always means movement in the declared improvement direction. The dashboard does not claim causality; it recommends a controlled, time-boxed experiment and contextual review.

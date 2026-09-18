package dev.engineeringintelligence.api.dashboard;

import dev.engineeringintelligence.api.config.AppProperties;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {
    private final JdbcClient jdbc;
    private final AppProperties app;
    private final OpportunityEngine opportunityEngine;

    public DashboardRepository(JdbcClient jdbc, AppProperties app, OpportunityEngine opportunityEngine) {
        this.jdbc = jdbc;
        this.app = app;
        this.opportunityEngine = opportunityEngine;
    }

    public Optional<Long> firstRepositoryId() {
        return jdbc.sql("SELECT repository_id FROM evidence.repository WHERE selected_for_sync ORDER BY full_name LIMIT 1")
            .query(Long.class).optional();
    }

    public DashboardMetrics load(long repositoryId) {
        String repository = jdbc.sql("SELECT full_name FROM evidence.repository WHERE repository_id=:id")
            .param("id", repositoryId).query(String.class).single();
        Map<String, Object> summary = jdbc.sql("""
            SELECT
              COALESCE(percentile_cont(.5) WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (merged_at-opened_at))/3600.0)
                FILTER (WHERE merged_at IS NOT NULL),0) cycle,
              COUNT(*) FILTER (WHERE state='OPEN') open_prs
            FROM evidence.pull_request WHERE repository_id=:id
            """).param("id", repositoryId).query().singleRow();
        double reviewWait = number(jdbc.sql("""
            SELECT COALESCE(percentile_cont(.5) WITHIN GROUP
              (ORDER BY EXTRACT(EPOCH FROM (fulfilled_at-requested_at))/3600.0),0) value
            FROM evidence.review_request rr JOIN evidence.pull_request pr USING (pull_request_id)
            WHERE pr.repository_id=:id AND fulfilled_at IS NOT NULL
            """).param("id", repositoryId).query().singleRow().get("value")).doubleValue();
        long openIssues = number(jdbc.sql("SELECT COUNT(*) value FROM evidence.issue WHERE repository_id=:id AND state='OPEN'")
            .param("id", repositoryId).query().singleRow().get("value")).longValue();
        double releasesPerMonth = number(jdbc.sql("""
            SELECT COUNT(*)/3.0 value FROM evidence.release
            WHERE repository_id=:id AND NOT prerelease AND published_at >= CURRENT_TIMESTAMP-INTERVAL '90 days'
            """).param("id", repositoryId).query().singleRow().get("value")).doubleValue();

        var outcomes = jdbc.sql("""
            SELECT b.metric_key,b.display_name,b.category,b.metric_value before_value,
                   c.metric_value after_value,b.unit,b.improvement_direction
            FROM analytics.metric_observation b
            JOIN analytics.measurement_period bp ON bp.period_id=b.period_id AND bp.period_kind='BASELINE'
            JOIN analytics.measurement_period cp ON cp.repository_id=bp.repository_id AND cp.period_kind='CURRENT'
            JOIN analytics.metric_observation c ON c.period_id=cp.period_id AND c.metric_key=b.metric_key
            WHERE bp.repository_id=:id ORDER BY b.category,b.display_name
            """).param("id", repositoryId).query((rs,n) -> {
                double before=rs.getDouble("before_value"), after=rs.getDouble("after_value");
                boolean higher="HIGHER".equals(rs.getString("improvement_direction"));
                double improvement=before==0?0:(higher?(after-before):(before-after))*100.0/before;
                return new DashboardMetrics.OutcomeComparison(rs.getString("metric_key"),rs.getString("display_name"),
                    rs.getString("category"),before,after,rs.getString("unit"),improvement);
            }).list();
        var adoption = jdbc.sql("""
            SELECT a.capability,a.adoption_percent,a.evidence_method
            FROM analytics.ai_adoption_snapshot a JOIN analytics.measurement_period p USING(period_id)
            WHERE p.repository_id=:id AND p.period_kind='CURRENT' ORDER BY a.capability
            """).param("id",repositoryId).query((rs,n) -> {
                double percent=rs.getDouble("adoption_percent");
                return new DashboardMetrics.AiAdoption(rs.getString("capability"),percent,
                    percent>=60?"HIGH":percent>=35?"MEDIUM":"LOW",rs.getString("evidence_method"));
            }).list();
        var opportunities=opportunityEngine.match(outcomes,adoption);

        var frequent = jdbc.sql("""
            SELECT fc.path, COUNT(*) changes, SUM(fc.additions+fc.deletions) churn
            FROM evidence.file_change fc JOIN evidence.pull_request pr USING (pull_request_id)
            WHERE pr.repository_id=:id GROUP BY fc.path ORDER BY changes DESC, churn DESC LIMIT 8
            """).param("id", repositoryId).query((rs,n) -> new DashboardMetrics.FileFrequency(
                rs.getString("path"), rs.getLong("changes"), rs.getLong("churn"))).list();
        var coverage = jdbc.sql("""
            SELECT DISTINCT ON (path) path, line_coverage, format FROM evidence.coverage_snapshot
            WHERE repository_id=:id ORDER BY path, captured_at DESC
            """).param("id", repositoryId).query((rs,n) -> new DashboardMetrics.CoverageRisk(
                rs.getString("path"), rs.getDouble("line_coverage")*100, rs.getString("format")))
            .list().stream().filter(row -> row.lineCoverage() < 70).toList();
        var risky = jdbc.sql("""
            SELECT number, additions+deletions size, changed_files,
              LEAST(100, ((additions+deletions)/10)+(changed_files*2)) score
            FROM evidence.pull_request WHERE repository_id=:id
              AND ((additions+deletions)>=400 OR changed_files>=15)
            ORDER BY score DESC LIMIT 8
            """).param("id", repositoryId).query((rs,n) -> {
                int score=rs.getInt("score");
                return new DashboardMetrics.PullRequestRisk(rs.getInt("number"),rs.getLong("size"),
                    rs.getInt("changed_files"),score,score>=80?"high":score>=50?"medium":"low");
            }).list();
        var aging = jdbc.sql("""
            SELECT bucket, COUNT(*) count FROM (
              SELECT CASE WHEN CURRENT_TIMESTAMP-created_at<INTERVAL '7 days' THEN '0–7 days'
                WHEN CURRENT_TIMESTAMP-created_at<INTERVAL '30 days' THEN '8–30 days'
                ELSE '31+ days' END bucket
              FROM evidence.issue WHERE repository_id=:id AND state='OPEN') ages
            GROUP BY bucket ORDER BY MIN(CASE bucket WHEN '0–7 days' THEN 1 WHEN '8–30 days' THEN 2 ELSE 3 END)
            """).param("id", repositoryId).query((rs,n) -> new DashboardMetrics.IssueAge(rs.getString("bucket"),rs.getLong("count"))).list();
        var flaky = jdbc.sql("""
            SELECT workflow_name, job_name, COUNT(DISTINCT head_sha) occurrences
            FROM evidence.workflow_job WHERE repository_id=:id GROUP BY workflow_name,job_name,head_sha
            HAVING BOOL_OR(conclusion='FAILURE') AND BOOL_OR(conclusion='SUCCESS')
            """).param("id", repositoryId).query((rs,n) -> new DashboardMetrics.FlakyJob(
                rs.getString("workflow_name"),rs.getString("job_name"),rs.getLong("occurrences"))).list();
        var bugs = jdbc.sql("""
            SELECT b.path, COUNT(*) linked_bugs FROM evidence.bug_file_link b
            JOIN evidence.issue i USING (issue_id) WHERE i.repository_id=:id AND i.is_bug
            GROUP BY b.path ORDER BY linked_bugs DESC, b.path LIMIT 8
            """).param("id", repositoryId).query((rs,n) -> new DashboardMetrics.BugHotspot(
                rs.getString("path"),rs.getLong("linked_bugs"))).list();

        Map<String,Object> workload = jdbc.sql("""
            SELECT COALESCE(SUM(reviewer_load),0) pending,
              COUNT(*) FILTER (WHERE reviewer_load>0) active,
              COALESCE(MAX(reviewer_load)*100.0/NULLIF(SUM(reviewer_load),0),0) concentration
            FROM (SELECT reviewer_id, COUNT(*) FILTER (WHERE fulfilled_at IS NULL) reviewer_load
              FROM evidence.review_request rr JOIN evidence.pull_request pr USING(pull_request_id)
              WHERE pr.repository_id=:id GROUP BY reviewer_id) loads
            """).param("id", repositoryId).query().singleRow();
        long active = number(workload.get("active")).longValue();
        boolean suppressed = active < app.security().minimumCohortSize();
        var signal = new DashboardMetrics.WorkloadSignal(number(workload.get("pending")).longValue(),active,
            suppressed?null:number(workload.get("concentration")).doubleValue(),suppressed,
            suppressed?"Hidden because the reviewer cohort is below the privacy threshold.":"A team resilience signal, not an individual performance measure.");
        return new DashboardMetrics(repositoryId,repository,
            new DashboardMetrics.Summary(number(summary.get("cycle")).doubleValue(),reviewWait,
                number(summary.get("open_prs")).longValue(),releasesPerMonth,openIssues),
            outcomes,adoption,opportunities,
            frequent,coverage,risky,aging,flaky,bugs,signal,
            new DashboardMetrics.DataQuality("Synthetic demo evidence","Generated at startup","Demonstration only",
                "Signals illustrate metric behavior and must not be used for employment decisions."));
    }

    private static Number number(Object value) { return value == null ? 0 : (Number)value; }
}

package dev.engineeringintelligence.api.dashboard;

import java.util.List;

public record DashboardMetrics(
    long repositoryId,
    String repository,
    Summary summary,
    List<OutcomeComparison> outcomeComparisons,
    List<AiAdoption> aiAdoption,
    List<Opportunity> opportunities,
    List<FileFrequency> frequentlyChangedFiles,
    List<CoverageRisk> lowCoverageAreas,
    List<PullRequestRisk> riskyPullRequests,
    List<IssueAge> issueAging,
    List<FlakyJob> flakyJobs,
    List<BugHotspot> bugHotspots,
    WorkloadSignal workload,
    DataQuality dataQuality
) {
    public record Summary(double medianCycleHours, double medianReviewWaitHours, long openPullRequests,
                          double releasesPerMonth, long openIssues) {}
    public record OutcomeComparison(String metricKey, String name, String category, double before,
                                    double after, String unit, double improvementPercent) {}
    public record AiAdoption(String capability, double adoptionPercent, String level, String evidenceMethod) {}
    public record Opportunity(String capability, String title, String why, String recommendation,
                              String confidence) {}
    public record FileFrequency(String path, long changes, long churn) {}
    public record CoverageRisk(String path, double lineCoverage, String format) {}
    public record PullRequestRisk(int number, long size, int changedFiles, int score, String level) {}
    public record IssueAge(String bucket, long count) {}
    public record FlakyJob(String workflow, String job, long occurrences) {}
    public record BugHotspot(String path, long linkedBugs) {}
    public record WorkloadSignal(long pendingReviews, long activeReviewers, Double concentrationPercent,
                                 boolean suppressed, String interpretation) {}
    public record DataQuality(String source, String freshness, String confidence, String limitation) {}
}

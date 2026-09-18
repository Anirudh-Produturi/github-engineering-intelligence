package dev.engineeringintelligence.api.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OpportunityEngine {
    public List<DashboardMetrics.Opportunity> match(List<DashboardMetrics.OutcomeComparison> outcomes,
                                                     List<DashboardMetrics.AiAdoption> adoption) {
        Map<String, DashboardMetrics.OutcomeComparison> metric = outcomes.stream()
            .collect(Collectors.toMap(DashboardMetrics.OutcomeComparison::metricKey, Function.identity()));
        Map<String, DashboardMetrics.AiAdoption> usage = adoption.stream()
            .collect(Collectors.toMap(DashboardMetrics.AiAdoption::capability, Function.identity()));
        List<DashboardMetrics.Opportunity> result = new ArrayList<>();
        if (low(usage,"TESTING") && after(metric,"test_coverage") < 80) result.add(new DashboardMetrics.Opportunity(
            "TESTING","Expand AI-assisted testing",
            "Coverage remains below the 80% demo target while testing-agent adoption is low.",
            "Run a four-week pilot on test generation and review generated tests in normal code review.","MEDIUM"));
        if (low(usage,"PR_REVIEW") && after(metric,"review_wait") > 8) result.add(new DashboardMetrics.Opportunity(
            "PR_REVIEW","Pilot AI-assisted PR preparation",
            "Median review wait remains above eight hours while PR-review-agent adoption is low.",
            "Pilot summaries, checklists, and reviewer routing; do not automate approval decisions.","MEDIUM"));
        if (low(usage,"CODING") && after(metric,"cycle_time") > 48) result.add(new DashboardMetrics.Opportunity(
            "CODING","Explore a focused coding-agent pilot",
            "Cycle time is elevated and coding-agent adoption is low.",
            "Choose one low-risk workflow and compare a time-boxed pilot with its baseline.","LOW"));
        return result;
    }

    private static boolean low(Map<String,DashboardMetrics.AiAdoption> values,String key){
        return !values.containsKey(key)||values.get(key).adoptionPercent()<50;
    }
    private static double after(Map<String,DashboardMetrics.OutcomeComparison> values,String key){
        return values.containsKey(key)?values.get(key).after():0;
    }
}

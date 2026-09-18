package dev.engineeringintelligence.api.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpportunityEngineTest {
    private final OpportunityEngine engine=new OpportunityEngine();

    @Test void connectsConstraintsToUnderusedCapabilities() {
        var outcomes=List.of(
            new DashboardMetrics.OutcomeComparison("test_coverage","Test coverage","QUALITY",62,76,"percent",22.6),
            new DashboardMetrics.OutcomeComparison("review_wait","Review wait","FLOW",20,12,"hours",40));
        var adoption=List.of(
            new DashboardMetrics.AiAdoption("TESTING",25,"LOW","survey"),
            new DashboardMetrics.AiAdoption("PR_REVIEW",20,"LOW","survey"));
        assertThat(engine.match(outcomes,adoption)).extracting(DashboardMetrics.Opportunity::capability)
            .containsExactly("TESTING","PR_REVIEW");
    }

    @Test void doesNotRecommendWhenTargetAndAdoptionAreHealthy() {
        var outcomes=List.of(new DashboardMetrics.OutcomeComparison("test_coverage","Test coverage","QUALITY",78,85,"percent",9));
        var adoption=List.of(new DashboardMetrics.AiAdoption("TESTING",70,"HIGH","survey"));
        assertThat(engine.match(outcomes,adoption)).isEmpty();
    }
}

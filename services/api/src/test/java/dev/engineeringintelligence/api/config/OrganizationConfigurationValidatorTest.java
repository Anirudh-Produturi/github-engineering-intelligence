package dev.engineeringintelligence.api.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.engineeringintelligence.api.github.GitHubWebhookProperties;
import org.junit.jupiter.api.Test;

class OrganizationConfigurationValidatorTest {
    @Test
    void permitsPersonalDemoConfiguration() {
        var app = properties(AppProperties.Mode.PERSONAL, true, "", 3);
        assertThatCode(() -> OrganizationConfigurationValidator.validate(app, github("short"))).doesNotThrowAnyException();
    }

    @Test
    void rejectsOrganizationModeWithoutOidc() {
        var app = properties(AppProperties.Mode.ORGANIZATION, false, "", 5);
        assertThatThrownBy(() -> OrganizationConfigurationValidator.validate(app, github("a-secure-webhook-secret-that-is-long-enough")))
            .hasMessageContaining("OIDC_ISSUER");
    }

    @Test
    void rejectsDemoDataInOrganizationMode() {
        var app = properties(AppProperties.Mode.ORGANIZATION, true, "https://identity.example.com", 5);
        assertThatThrownBy(() -> OrganizationConfigurationValidator.validate(app, github("a-secure-webhook-secret-that-is-long-enough")))
            .hasMessageContaining("Demo data");
    }

    @Test
    void acceptsSecureOrganizationConfiguration() {
        var app = properties(AppProperties.Mode.ORGANIZATION, false, "https://identity.example.com", 5);
        assertThatCode(() -> OrganizationConfigurationValidator.validate(app, github("a-secure-webhook-secret-that-is-long-enough")))
            .doesNotThrowAnyException();
    }

    private static AppProperties properties(AppProperties.Mode mode, boolean demo, String issuer, int cohort) {
        return new AppProperties(mode, new AppProperties.Demo(demo), new AppProperties.Security(issuer, cohort));
    }

    private static GitHubWebhookProperties github(String secret) {
        return new GitHubWebhookProperties(secret);
    }
}

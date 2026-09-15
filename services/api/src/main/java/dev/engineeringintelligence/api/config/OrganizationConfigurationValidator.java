package dev.engineeringintelligence.api.config;

import dev.engineeringintelligence.api.github.GitHubWebhookProperties;

final class OrganizationConfigurationValidator {
    private static final String DEVELOPMENT_SECRET = "personal-development-secret-change-before-sharing";

    private OrganizationConfigurationValidator() {}

    static void validate(AppProperties app, GitHubWebhookProperties github) {
        if (app.security().minimumCohortSize() < 2) {
            throw new IllegalStateException("app.security.minimum-cohort-size must be at least 2");
        }
        if (app.mode() != AppProperties.Mode.ORGANIZATION) return;
        if (app.demo().enabled()) {
            throw new IllegalStateException("Demo data must be disabled in organization mode");
        }
        if (app.security().oidcIssuer() == null || app.security().oidcIssuer().isBlank()) {
            throw new IllegalStateException("OIDC_ISSUER is required in organization mode");
        }
        if (github.webhookSecret().equals(DEVELOPMENT_SECRET) || github.webhookSecret().length() < 32) {
            throw new IllegalStateException("A non-development GitHub webhook secret of at least 32 characters is required in organization mode");
        }
    }
}


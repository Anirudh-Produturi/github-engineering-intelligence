package dev.engineeringintelligence.api.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
public record GitHubWebhookProperties(String webhookSecret) {
    public GitHubWebhookProperties {
        if (webhookSecret == null || webhookSecret.isBlank()) throw new IllegalArgumentException("github.webhook-secret must be configured");
    }
}


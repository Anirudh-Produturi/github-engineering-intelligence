package dev.engineeringintelligence.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Mode mode, Demo demo, Security security) {
    public enum Mode { PERSONAL, ORGANIZATION }

    public record Demo(boolean enabled) {}

    public record Security(String oidcIssuer, int minimumCohortSize) {}
}


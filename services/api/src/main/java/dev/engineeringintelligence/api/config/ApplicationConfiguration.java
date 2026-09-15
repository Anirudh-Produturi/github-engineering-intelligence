package dev.engineeringintelligence.api.config;

import dev.engineeringintelligence.api.github.GitHubWebhookProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class ApplicationConfiguration {
    @Bean
    ApplicationRunner validateOrganizationConfiguration(AppProperties app, GitHubWebhookProperties github) {
        return arguments -> OrganizationConfigurationValidator.validate(app, github);
    }
}


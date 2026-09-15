package dev.engineeringintelligence.api.github;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GitHubWebhookProperties.class)
public class GitHubConfiguration {}


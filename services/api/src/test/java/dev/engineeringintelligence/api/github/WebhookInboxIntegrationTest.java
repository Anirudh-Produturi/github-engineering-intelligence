package dev.engineeringintelligence.api.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class WebhookInboxIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("github.webhook-secret", () -> "test-secret");
    }

    @Autowired WebhookInboxRepository repository;

    @Test
    void deliveryIdMakesInboxIdempotent() {
        assertThat(repository.store("delivery-1", "pull_request", "{\"action\":\"opened\"}", null)).isTrue();
        assertThat(repository.store("delivery-1", "pull_request", "{\"action\":\"opened\"}", null)).isFalse();
    }
}

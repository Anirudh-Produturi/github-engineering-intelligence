package dev.engineeringintelligence.api.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {
    static final long ACCOUNT_ID = 9_000_001L;
    static final long INSTALLATION_ID = 9_000_001L;
    static final long REPOSITORY_ID = 9_000_001L;

    private final JdbcClient jdbc;

    public DemoDataSeeder(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        jdbc.sql("""
            INSERT INTO integration.github_account (account_id, login, account_type)
            VALUES (:id, 'acme-demo', 'ORGANIZATION')
            ON CONFLICT (account_id) DO NOTHING
            """).param("id", ACCOUNT_ID).update();
        jdbc.sql("""
            INSERT INTO integration.github_installation (installation_id, account_id, installed_at)
            VALUES (:id, :accountId, TIMESTAMPTZ '2026-01-01 00:00:00Z')
            ON CONFLICT (installation_id) DO NOTHING
            """).param("id", INSTALLATION_ID).param("accountId", ACCOUNT_ID).update();
        jdbc.sql("""
            INSERT INTO evidence.repository
                (repository_id, installation_id, account_id, name, full_name, default_branch, private)
            VALUES (:id, :installationId, :accountId, 'checkout-service', 'acme-demo/checkout-service', 'main', false)
            ON CONFLICT (repository_id) DO NOTHING
            """).param("id", REPOSITORY_ID)
                .param("installationId", INSTALLATION_ID)
                .param("accountId", ACCOUNT_ID)
                .update();
    }
}


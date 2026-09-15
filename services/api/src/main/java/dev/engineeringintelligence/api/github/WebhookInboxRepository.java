package dev.engineeringintelligence.api.github;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class WebhookInboxRepository {
    private final JdbcClient jdbc;

    public WebhookInboxRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public boolean store(String deliveryId, String eventType, String payload, Long installationId) {
        int rows = jdbc.sql("""
                INSERT INTO integration.webhook_delivery (delivery_id, event_type, payload, installation_id)
                VALUES (:deliveryId, :eventType, CAST(:payload AS jsonb), :installationId)
                ON CONFLICT (delivery_id) DO NOTHING
                """)
            .param("deliveryId", deliveryId)
            .param("eventType", eventType)
            .param("payload", payload)
            .param("installationId", installationId)
            .update();
        return rows == 1;
    }
}

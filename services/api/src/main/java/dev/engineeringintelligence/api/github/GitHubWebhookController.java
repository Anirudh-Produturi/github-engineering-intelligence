package dev.engineeringintelligence.api.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.engineeringintelligence.api.config.AppProperties;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/github/webhooks")
public class GitHubWebhookController {
    private final WebhookSignatureVerifier verifier;
    private final WebhookInboxRepository inbox;
    private final ObjectMapper objectMapper;
    private final AppProperties app;

    public GitHubWebhookController(WebhookSignatureVerifier verifier, WebhookInboxRepository inbox, ObjectMapper objectMapper, AppProperties app) {
        this.verifier = verifier;
        this.inbox = inbox;
        this.objectMapper = objectMapper;
        this.app = app;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, String>> receive(
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestBody byte[] body) {
        if (!verifier.isValid(body, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "invalid_signature"));
        }
        Long installationId;
        try {
            JsonNode root = objectMapper.readTree(body);
            installationId = root.path("installation").path("id").canConvertToLong()
                ? root.path("installation").path("id").longValue()
                : null;
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(Map.of("status", "invalid_json"));
        }
        if (app.mode() == AppProperties.Mode.ORGANIZATION && installationId == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "missing_installation"));
        }
        boolean created = inbox.store(deliveryId, eventType, new String(body, StandardCharsets.UTF_8), installationId);
        return ResponseEntity.status(created ? HttpStatus.ACCEPTED : HttpStatus.OK)
            .body(Map.of("status", created ? "accepted" : "duplicate"));
    }
}

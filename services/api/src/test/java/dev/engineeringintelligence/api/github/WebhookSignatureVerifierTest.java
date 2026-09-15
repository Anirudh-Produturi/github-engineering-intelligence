package dev.engineeringintelligence.api.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WebhookSignatureVerifierTest {
    private final WebhookSignatureVerifier verifier = new WebhookSignatureVerifier(new GitHubWebhookProperties("It's a Secret to Everybody"));

    @Test
    void acceptsGitHubPublishedSignatureVector() {
        byte[] payload = "Hello, World!".getBytes();
        assertThat(verifier.isValid(payload, "sha256=757107ea0eb2509fc211221cce984b8a37570b6d7586c22c46f4379c8b043e17")).isTrue();
    }

    @Test
    void rejectsMissingOrIncorrectSignatures() {
        byte[] payload = "Hello, World!".getBytes();
        assertThat(verifier.isValid(payload, null)).isFalse();
        assertThat(verifier.isValid(payload, "sha256=deadbeef")).isFalse();
        assertThat(verifier.isValid(payload, "sha1=757107ea")).isFalse();
    }
}


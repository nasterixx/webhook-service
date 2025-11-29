package com.example.webhook.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CallbackService {

    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);

    private final WebClient webClient;

    public CallbackService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> sendSuccess(String url, String traceId, Object result) {
        if (url == null || url.isBlank()) {
            log.info("[Callback] No callbackUrl specified; skipping SUCCESS callback");
            return Mono.empty();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("traceId", traceId);
        body.put("status", "SUCCESS");
        body.put("result", result);
        body.put("timestamp", Instant.now().toString());

        log.info("[Callback] Sending SUCCESS callback to {}", url);

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("[Callback] SUCCESS callback delivered"))
                .doOnError(ex -> log.error("[Callback] Failed to deliver SUCCESS callback: {}", ex.toString()))
                .onErrorResume(ex -> Mono.empty());
    }

    public Mono<Void> sendFailure(String url, String traceId, Throwable ex) {
        if (url == null || url.isBlank()) {
            log.info("[Callback] No callbackUrl specified; skipping ERROR callback");
            return Mono.empty();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("traceId", traceId);
        body.put("status", "ERROR");
        body.put("error", ex.getMessage());
        body.put("timestamp", Instant.now().toString());

        log.info("[Callback] Sending ERROR callback to {}", url);

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("[Callback] ERROR callback delivered"))
                .doOnError(e -> log.error("[Callback] Failed to deliver ERROR callback: {}", e.toString()))
                .onErrorResume(e -> Mono.empty());
    }
}

package com.example.webhook.api;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.model.common.WebhookRequestV1;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/webhook")
public class GenericWebhookController {

    private final WebhookDispatcher dispatcher;

    public GenericWebhookController(WebhookDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Object>>> receive(
            @PathVariable("version") String version,
            @RequestBody Mono<WebhookRequestV1> requestMono,
            ServerHttpRequest httpRequest
    ) {
        long start = System.currentTimeMillis();
        String requestId = httpRequest.getHeaders().getFirst("X-Request-Id");
        String traceId = UUID.randomUUID().toString();

        return requestMono
                .flatMap(dispatcher::dispatch)   // Mono<Object>
                .map(result -> {
                    ApiResponse<Object> response = new ApiResponse<>(
                            Instant.now(),
                            "SUCCESS",
                            "Webhook processed successfully (" + version + ")",
                            requestId,
                            UUID.randomUUID().toString(),
                            System.currentTimeMillis() - start,
                            result
                    );
                    return ResponseEntity.ok(response);
                });
    }
}

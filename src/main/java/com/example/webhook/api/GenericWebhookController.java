package com.example.webhook.api;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.integrations.CallbackService;
import com.example.webhook.model.common.WebhookRequestV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.webhook.security.CorrelationIdFilter.CTX_REQUEST_ID;
import static com.example.webhook.security.CorrelationIdFilter.CTX_TRACE_ID;

@RestController
@RequestMapping("/api/{version}/webhook")
public class GenericWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GenericWebhookController.class);

    private final WebhookDispatcher dispatcher;
    private final CallbackService callbackService;

    public GenericWebhookController(WebhookDispatcher dispatcher,
                                    CallbackService callbackService) {
        this.dispatcher = dispatcher;
        this.callbackService = callbackService;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> receive(
            @PathVariable("version") String version,
            @RequestBody Mono<WebhookRequestV1> requestMono
    ) {
        long start = System.currentTimeMillis();

        return Mono.deferContextual(ctx ->
                requestMono.flatMap(request -> {

                    // Extract IDs
                    String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
                    String traceId = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

                    // Extract callbackUrl
                    final String callbackUrl = (request.data() != null &&
                            request.data().attributes() != null &&
                            request.data().attributes().payload() != null &&
                            request.data().attributes().payload().get("callbackUrl") != null)
                            ? request.data().attributes().payload().get("callbackUrl").toString()
                            : null;

                    // Fire-and-forget
                    Mono.just(request)
                            .flatMap(r -> dispatcher.dispatch(r))
                            .flatMap(result -> callbackService.sendSuccess(callbackUrl, traceId, result))
                            .onErrorResume(ex -> callbackService.sendFailure(callbackUrl, traceId, ex))
                            .contextWrite(ctx)
                            .subscribe();

                    ApiResponse response = new ApiResponse(
                            Instant.now(),
                            "ACCEPTED",
                            "Webhook received successfully (processing asynchronously: " + version + ")",
                            requestId,
                            traceId,
                            System.currentTimeMillis() - start,
                            null
                    );

                    return Mono.just(ResponseEntity.ok(response));
                })
        );
    }

    @PostMapping(value = "/completed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void completed(
            @RequestBody String json
    ) {
        System.out.println(json);
    }

}

package com.example.webhook.api;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.integrations.CallbackService;
import com.example.webhook.model.common.WebhookRequestV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

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
    public Mono<ResponseEntity<ApiResponse<Object>>> receive(
            @PathVariable("version") String version,
            @RequestBody Mono<WebhookRequestV1> requestMono
    ) {
        long start = System.currentTimeMillis();

        return Mono.deferContextual(ctx ->
                requestMono
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is missing")))
                        .flatMap(request -> {

                            String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
                            String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

                            // Extract callbackUrl
                            String callbackUrl = null;
                            try {
                                if (request.data() != null &&
                                        request.data().attributes() != null &&
                                        request.data().attributes().payload() != null) {
                                    Object cb = request.data().attributes().payload().get("callbackUrl");
                                    if (cb != null) {
                                        callbackUrl = cb.toString();
                                    }
                                }
                            } catch (Exception e) {
                                return Mono.error(new IllegalArgumentException("Invalid callbackUrl format"));
                            }
                            final String finalCallbackUrl = callbackUrl;

                            log.info("[Webhook] Accepted version={} requestId={} traceId={} callbackUrl={}",
                                    version, requestId, traceId, finalCallbackUrl);

                            // Fire-and-forget in background pool
                            Schedulers.boundedElastic().schedule(() -> {
                                try {
                                    dispatcher.dispatch(request)
                                            .flatMap(result -> callbackService.sendSuccess(finalCallbackUrl, traceId, result))
                                            .onErrorResume(ex -> callbackService.sendFailure(finalCallbackUrl, traceId, ex))
                                            .contextWrite(ctx)
                                            .block();
                                } catch (Throwable t) {
                                    log.error("[Webhook] Error in background processing: {}", t.toString(), t);
                                }
                            });

                            ApiResponse<Object> ok = new ApiResponse<>(
                                    Instant.now(),
                                    "ACCEPTED",
                                    "Webhook received successfully (processing asynchronously: " + version + ")",
                                    requestId,
                                    traceId,
                                    System.currentTimeMillis() - start,
                                    null
                            );

                            return Mono.just(ResponseEntity.ok(ok));
                        })
                        .onErrorResume(ex -> {
                            String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
                            String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

                            ApiResponse<Object> err = new ApiResponse<>(
                                    Instant.now(),
                                    "ERROR",
                                    ex.getMessage(),
                                    requestId,
                                    traceId,
                                    System.currentTimeMillis() - start,
                                    null
                            );

                            if (ex instanceof IllegalArgumentException) {
                                return Mono.just(ResponseEntity.badRequest().body(err));
                            }
                            return Mono.just(ResponseEntity.internalServerError().body(err));
                        })
        );
    }
}

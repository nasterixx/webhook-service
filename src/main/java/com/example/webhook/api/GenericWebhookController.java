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
import java.util.Map;

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

//    @PostMapping
//    public Mono<ResponseEntity<ApiResponse>> receive(
//            @PathVariable("version") String version,
//            @RequestBody WebhookRequestV1 request
//    ) {
//        long start = System.currentTimeMillis();
//
//        return Mono.deferContextual(ctx -> {
//
//            String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
//            String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");
//
//            // Extract callback URL safely
//            String callbackUrl = null;
//            if (request != null &&
//                    request.data() != null &&
//                    request.data().attributes() != null &&
//                    request.data().attributes().payload() != null) {
//
//                Object cb = request.data().attributes().payload().get("callbackUrl");
//                if (cb != null) callbackUrl = cb.toString();
//            }
//            final String finalCallbackUrl = callbackUrl;
//
//            // 🚀 FULL FIRE-AND-FORGET PIPELINE (runs on separate thread)
//            Mono.defer(() ->
//                            dispatcher.dispatch(request)
//                                    .flatMap(result -> callbackService.sendSuccess(finalCallbackUrl, traceId, result))
//                                    .onErrorResume(ex -> callbackService.sendFailure(finalCallbackUrl, traceId, ex))
//                                    .contextWrite(ctx)
//                    )
//                    .subscribeOn(Schedulers.boundedElastic())     // <-- MAGIC LINE
//                    .subscribe();
//
//            // 🚀 IMMEDIATE RESPONSE
//            ApiResponse response = new ApiResponse(
//                    Instant.now(),
//                    "ACCEPTED",
//                    "Webhook received successfully (processing asynchronously: " + version + ")",
//                    requestId,
//                    traceId,
//                    System.currentTimeMillis() - start,
//                    null
//            );
//
//            return Mono.just(ResponseEntity.ok(response));
//        });
//    }

//    @PostMapping
//    public Mono<ResponseEntity<ApiResponse>> receive(
//            @PathVariable("version") String version,
//            @RequestBody WebhookRequestV1 request
//    ) {
//        long start = System.currentTimeMillis();
//
//        return Mono.deferContextual(ctx -> {
//
//            String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
//            String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");
//
//            // Extract callback URL safely
//            String callbackUrl = null;
//            if (request != null &&
//                    request.data() != null &&
//                    request.data().attributes() != null &&
//                    request.data().attributes().payload() != null) {
//
//                Object cb = request.data().attributes().payload().get("callbackUrl");
//                if (cb != null) callbackUrl = cb.toString();
//            }
//            final String finalCallbackUrl = callbackUrl;
//
//            // 🚀 FULL FIRE-AND-FORGET PIPELINE (runs on separate thread)
//            Mono.defer(() ->
//                            dispatcher.dispatch(request)
//                                    .flatMap(result -> callbackService.sendSuccess(finalCallbackUrl, traceId, result))
//                                    .onErrorResume(ex -> callbackService.sendFailure(finalCallbackUrl, traceId, ex))
//                                    .contextWrite(ctx)
//                    )
//                    .subscribeOn(Schedulers.boundedElastic())     // <-- MAGIC LINE
//                    .subscribe();
//
//            // 🚀 IMMEDIATE RESPONSE
//            ApiResponse response = new ApiResponse(
//                    Instant.now(),
//                    "ACCEPTED",
//                    "Webhook received successfully (processing asynchronously: " + version + ")",
//                    requestId,
//                    traceId,
//                    System.currentTimeMillis() - start,
//                    null
//            );
//
//            return Mono.just(ResponseEntity.ok(response));
//        });
//    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> receive(
            @PathVariable("version") String version,
            @RequestBody WebhookRequestV1 request
    ) {
        long start = System.currentTimeMillis();

        return Mono.deferContextual(ctx -> {

            String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
            String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

            String callbackUrl = null;
            try {
                if (request != null &&
                        request.data() != null &&
                        request.data().attributes() != null &&
                        request.data().attributes().payload() != null) {

                    Object cb = request.data().attributes().payload().get("callbackUrl");
                    if (cb != null) callbackUrl = cb.toString();
                }
            } catch (Exception e) {
                log.warn("[Webhook] unable to extract callbackUrl: {}", e.toString());
            }
            final String finalCallbackUrl = callbackUrl;

            // 🔥 Hard async break: move everything to boundedElastic BEFORE dispatching
            Mono.fromRunnable(() -> { /* async separation */ })
                    .subscribeOn(Schedulers.boundedElastic())
                    .then(
                            dispatcher.dispatch(request)
                                    .flatMap(result -> callbackService.sendSuccess(finalCallbackUrl, traceId, result))
                                    .onErrorResume(ex -> callbackService.sendFailure(finalCallbackUrl, traceId, ex))
                                    .contextWrite(ctx)
                    )
                    .subscribe();

            // Return immediately
            ApiResponse body = new ApiResponse(
                    Instant.now(),
                    "ACCEPTED",
                    "Webhook received successfully (processing asynchronously: " + version + ")",
                    requestId,
                    traceId,
                    System.currentTimeMillis() - start,
                    null
            );

            return Mono.just(ResponseEntity.ok(body));
        });
    }


}

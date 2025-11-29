package com.example.webhook.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.time.Instant;

import static com.example.webhook.security.CorrelationIdFilter.CTX_REQUEST_ID;
import static com.example.webhook.security.CorrelationIdFilter.CTX_TRACE_ID;

@RestControllerAdvice
public class GlobalErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handle(Throwable ex) {
        log.error("Global error caught: {}", ex.toString(), ex);

        return Mono.deferContextual(ctx -> Mono.just(buildErrorResponse(ex, ctx)));
    }

    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(
            Throwable ex,
            ContextView ctx
    ) {
        String requestId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
        String traceId   = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

        ApiResponse<Object> body = new ApiResponse<>(
                Instant.now(),
                "ERROR",
                ex.getMessage(),
                requestId,
                traceId,
                0,
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

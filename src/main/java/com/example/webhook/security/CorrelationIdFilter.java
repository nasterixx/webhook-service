package com.example.webhook.security;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements WebFilter {

    public static final String CTX_REQUEST_ID = "requestId";
    public static final String CTX_TRACE_ID   = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();

        String requestId = req.getHeaders().getFirst("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String traceId = UUID.randomUUID().toString();

        MDC.put(CTX_REQUEST_ID, requestId);
        MDC.put(CTX_TRACE_ID, traceId);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx
//                        .put(CTX_REQUEST_ID, requestId)
                        .put(CTX_TRACE_ID, traceId)
                )
                .doFinally(sig -> MDC.clear());
    }
}

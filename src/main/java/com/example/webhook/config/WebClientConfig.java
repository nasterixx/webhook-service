package com.example.webhook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.example.webhook.security.CorrelationIdFilter.CTX_REQUEST_ID;
import static com.example.webhook.security.CorrelationIdFilter.CTX_TRACE_ID;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter((request, next) ->
                        Mono.deferContextual(ctx -> {
                            String reqId = ctx.getOrDefault(CTX_REQUEST_ID, "unknown");
                            String traceId = ctx.getOrDefault(CTX_TRACE_ID, "unknown");

                            ClientRequest newReq = ClientRequest.from(request)
                                    .header("X-Request-Id", reqId)
                                    .header("X-Trace-Id", traceId)
                                    .build();

                            return next.exchange(newReq);
                        })
                )
                .build();
    }
}

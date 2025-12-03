package com.example.webhook.integrations;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ModuleCClient {

    private static final Logger log = LoggerFactory.getLogger(ModuleCClient.class);

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public ModuleCClient(WebClient webClient, CircuitBreakerRegistry cbRegistry) {
        this.webClient = webClient;
        this.circuitBreaker = cbRegistry.circuitBreaker("moduleC");
    }

    public Mono<String> initiateProcessing(String ns3Location) {
        log.info("Calling Module C with ns3Location={}", ns3Location);

        String url = "https://example.com/module-c/process"; // example URL

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"ns3Location\":\"" + ns3Location + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorMap(ex -> new RuntimeException("Module C unavailable or failed", ex));
    }
}

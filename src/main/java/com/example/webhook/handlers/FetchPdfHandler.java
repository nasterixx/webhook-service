package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.model.payload.PurchaseCreatedPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("fetchPdfHandler")
public class FetchPdfHandler implements ReactiveWebhookHandler<PurchaseCreatedPayload, byte[]> {

    private final WebClient webClient;

    public FetchPdfHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<byte[]> handle(PurchaseCreatedPayload payload) {
        if (payload.getFilename() == null || payload.getFilename().isBlank()) {
            return Mono.error(new IllegalArgumentException("filename is required to fetch PDF"));
        }

        return webClient.get()
                .uri(payload.getFilename())
                .retrieve()
                .bodyToMono(byte[].class);
    }
}

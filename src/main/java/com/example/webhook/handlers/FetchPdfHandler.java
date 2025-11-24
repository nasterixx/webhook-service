package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.model.payload.LabelCreatedPayload;
import com.example.webhook.model.payload.OrderCreatedPayload;
import com.example.webhook.model.payload.Payload;
import com.example.webhook.model.payload.PurchaseCreatedPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("fetchPdfHandler")
public class FetchPdfHandler implements ReactiveWebhookHandler<Payload, byte[]> {

    private final WebClient webClient;

    public FetchPdfHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<byte[]> handle(Payload payload) {

        return switch (payload) {
            case PurchaseCreatedPayload purchaseCreatedPayload -> getPdfBytes(purchaseCreatedPayload.getFilename());
            case OrderCreatedPayload purchaseCreatedPayload -> getPdfBytes(purchaseCreatedPayload.getFilename());
            case LabelCreatedPayload purchaseCreatedPayload -> getPdfBytes(purchaseCreatedPayload.getFilename());
            default -> throw new IllegalStateException("Unexpected value: " + payload);
        };
//        return getPdfBytes(payload);
    }

    private Mono<byte[]> getPdfBytes(String filename) {
        if (filename == null || filename.isBlank()) {
            return Mono.error(new IllegalArgumentException("filename is required to fetch PDF"));
        }

        return webClient.get()
                .uri(filename)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}

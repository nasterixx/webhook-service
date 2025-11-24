package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.integrations.StorageService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("storeNs3Handler")
public class StoreNs3Handler implements ReactiveWebhookHandler<byte[], String> {

    private final StorageService storageService;

    public StoreNs3Handler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Mono<String> handle(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            return Mono.error(new IllegalStateException("PDF bytes are empty; cannot store"));
        }
        String key = "purchase-pdf";
        return storageService.upload(key, pdfBytes);
    }
}

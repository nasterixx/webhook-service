package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.integrations.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("storeNs3Handler")
public class StoreNs3Handler implements ReactiveWebhookHandler<byte[], String> {

    private static final Logger log = LoggerFactory.getLogger(StoreNs3Handler.class);
    private final StorageService storageService;

    public StoreNs3Handler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Mono<String> handle(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            return Mono.error(new StorageException("Cannot store empty PDF bytes"));
        }

        String key = "purchase-pdf";
        log.info("[StoreNs3Handler] Uploading PDF as key {}", key);

        return storageService.upload(key, pdfBytes)
                .doOnError(ex -> log.error("[StoreNs3Handler] Upload failed: {}", ex.toString()))
                .onErrorMap(ex -> new StorageException("Failed to upload to NS3", ex));
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String msg) { super(msg); }
        public StorageException(String msg, Throwable cause) { super(msg, cause); }
    }
}

package com.example.webhook.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Stub example for NS3 (S3-like) storage.
 * Replace with real SDK integration (e.g. AWS SDK, MinIO) as needed.
 */
@Component
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    public Mono<String> upload(String key, byte[] bytes) {
        String objectUri = "ns3://bucket/" + key + ".pdf";
        log.info("Storing {} bytes to {}", (bytes == null ? 0 : bytes.length), objectUri);
        // Perform real upload here...
        return Mono.just(objectUri);
    }
}

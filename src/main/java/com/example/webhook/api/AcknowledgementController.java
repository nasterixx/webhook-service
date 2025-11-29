package com.example.webhook.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/ack")
public class AcknowledgementController {

    private static final Logger log = LoggerFactory.getLogger(AcknowledgementController.class);

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> receiveAck(
            @RequestBody Mono<Map<String, Object>> ackMono
    ) {
        return ackMono
                .doOnNext(ack ->
                        log.info("<<< RECEIVED ACK >>> at {} payload={}",
                                Instant.now(), ack)
                )
                .map(ack -> ResponseEntity.ok(
                        Map.of(
                                "status", "ACK_RECEIVED",
                                "receivedAt", Instant.now().toString()
                        )
                ));
    }
}

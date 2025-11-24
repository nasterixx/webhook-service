package com.example.webhook.api;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.core.map.WebhookRequestMapper;
import com.example.webhook.model.common.WebhookRequestV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/{version}")
public class GenericWebhookController {

    private final WebhookRequestMapper mapper;
    private final WebhookDispatcher dispatcher;

    public GenericWebhookController(WebhookRequestMapper mapper, WebhookDispatcher dispatcher) {
        this.mapper = mapper;
        this.dispatcher = dispatcher;
    }

    /*@PostMapping
    public Mono<ResponseEntity<String>> receive(
            @PathVariable("version") String version,
            @RequestBody String body) {

        return Mono.fromCallable(() -> (WebhookRequestV1<?>) mapper.map(body))
                .flatMap(dispatcher::dispatch)
                .map(status -> ResponseEntity.ok("Processed successfully (" + version + "): " + status));
    }*/

    @PostMapping
    public Mono<ResponseEntity<String>> receive(
            @PathVariable("version") String version,
            @RequestBody String body) {

        return Mono.fromCallable(() -> (WebhookRequestV1<?>) mapper.map(body))
                .flatMap(dispatcher::dispatch)
                .map(status -> ResponseEntity.ok("Processed successfully (" + version + "): " + status));
    }
}

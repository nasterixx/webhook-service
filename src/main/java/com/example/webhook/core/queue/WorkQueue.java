package com.example.webhook.core.queue;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class WorkQueue {

    private final Sinks.Many<QueuedWebhook> sink =
            Sinks.many().unicast().onBackpressureBuffer();

    public void submit(QueuedWebhook webhook) {
        sink.tryEmitNext(webhook);
    }

    public Flux<QueuedWebhook> stream() {
        return sink.asFlux();
    }
}

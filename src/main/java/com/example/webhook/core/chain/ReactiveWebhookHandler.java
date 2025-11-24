package com.example.webhook.core.chain;

import reactor.core.publisher.Mono;

public interface ReactiveWebhookHandler<I, O> {
    Mono<O> handle(I input);
}

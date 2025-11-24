package com.example.webhook.core.chain;

import reactor.core.publisher.Mono;

public interface ReactiveHandlerChain<I, O> {
    Mono<O> execute(I input);
}

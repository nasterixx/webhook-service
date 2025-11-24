package com.example.webhook.core.chain;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Dynamic reactive chain implementation operating on Object -> Object.
 * Type compatibility between handlers is guaranteed by configuration.
 */
public class GenericReactiveHandlerChain implements ReactiveHandlerChain<Object, Object> {

    private final List<ReactiveWebhookHandler<Object, Object>> handlers;

    public GenericReactiveHandlerChain(List<ReactiveWebhookHandler<Object, Object>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Mono<Object> execute(Object input) {
        Mono<Object> result = Mono.just(input);
        for (ReactiveWebhookHandler<Object, Object> handler : handlers) {
            result = result.flatMap(handler::handle);
        }
        return result;
    }
}

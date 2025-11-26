package com.example.webhook.core.dispatch;

import com.example.webhook.core.chain.GenericReactiveHandlerChain;
import com.example.webhook.core.properties.RetryProperties;
import com.example.webhook.core.properties.WebhookSchemaProperties;
import com.example.webhook.model.common.WebhookRequestV1;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebhookDispatcher {

    private final ReactiveChainBuilder chainBuilder;
    private final RetryProperties retryProperties;

    public WebhookDispatcher(ReactiveChainBuilder chainBuilder, RetryProperties retryProperties) {
        this.chainBuilder = chainBuilder;
        this.retryProperties = retryProperties;
    }

    /**
     * Execute a full reactive chain. Per-schema retry:
     * - If schema.retry.enabled == true, choose strategy (fixed, backoff, jitter)
     *   and only retry on exceptions listed in schema.retry.retryOn.
     * - If missing or false, no retry.
     */
    public Mono<Object> dispatch(WebhookRequestV1 request) {
        String schema = request.data().attributes().contentSchema();
        Object input = request.data().attributes().payload();

        GenericReactiveHandlerChain chain = chainBuilder.buildChain(schema);
        WebhookSchemaProperties.SchemaMapping mapping = chainBuilder.getSchemaMapping(schema);

        Mono<Object> pipeline = chain.execute(input);

        WebhookSchemaProperties.SchemaRetry schemaRetry = mapping.getRetry();
        boolean retryEnabled = schemaRetry != null &&
                Boolean.TRUE.equals(schemaRetry.getEnabled());

        if (!retryEnabled) {
            return pipeline;
        }

        String strategy = (schemaRetry.getStrategy() != null)
                ? schemaRetry.getStrategy().toLowerCase()
                : "backoff";

        Retry retrySpec = buildRetrySpec(strategy);

        // Build list of retryable exception classes
        List<Class<? extends Throwable>> retryable = new ArrayList<>();
//        if (schemaRetry.getRetryOn() != null) {
//            for (String cn : schemaRetry.getRetryOn()) {
//                try {
//                    Class<?> clazz = Class.forName(cn);
//                    if (Throwable.class.isAssignableFrom(clazz)) {
//                        @SuppressWarnings("unchecked")
//                        Class<? extends Throwable> c = (Class<? extends Throwable>) clazz;
//                        retryable.add(c);
//                    }
//                } catch (ClassNotFoundException ignored) {}
//            }
//        }

        // If enabled but no retryOn specified, fallback to retry on all exceptions
        return pipeline.retryWhen(retrySpec);

//        return pipeline.retryWhen(
//                retrySpec.filter(ex -> {
//                    for (Class<? extends Throwable> c : retryable) {
//                        if (c.isInstance(ex)) {
//                            return true;
//                        }
//                    }
//                    return false;
//                })
//        );
    }

    private Retry buildRetrySpec(String strategy) {
        return switch (strategy) {
            case "fixed" -> Retry.fixedDelay(
                    retryProperties.getMaxAttempts(),
                    Duration.ofMillis(retryProperties.getInitialDelayMs())
            );
            case "jitter" -> Retry.backoff(
                            retryProperties.getMaxAttempts(),
                            Duration.ofMillis(retryProperties.getInitialDelayMs()))
                    .jitter(0.5);
//            case "backoff":
            default -> Retry.backoff(
                    retryProperties.getMaxAttempts(),
                    Duration.ofMillis(retryProperties.getInitialDelayMs()));
        };
    }
}

package com.example.webhook.core.dispatch;

import com.example.webhook.core.chain.GenericReactiveHandlerChain;
import com.example.webhook.core.properties.RetryProperties;
import com.example.webhook.core.properties.WebhookSchemaProperties;
import com.example.webhook.model.common.WebhookRequestV1;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class WebhookDispatcher {

    private final ReactiveChainBuilder chainBuilder;
    private final RetryProperties retryProperties;

    public WebhookDispatcher(ReactiveChainBuilder chainBuilder,
                             RetryProperties retryProperties) {
        this.chainBuilder = chainBuilder;
        this.retryProperties = retryProperties;
    }

    public Mono<Object> dispatch(WebhookRequestV1 request) {
        String schema = request.data().attributes().contentSchema();
        Object input = request.data().attributes().payload();

        GenericReactiveHandlerChain chain = chainBuilder.buildChain(schema);
        WebhookSchemaProperties.SchemaMapping mapping =
                chainBuilder.getSchemaMapping(schema);

        Mono<Object> pipeline = chain.execute(input);

        WebhookSchemaProperties.SchemaRetry schemaRetry = mapping.getRetry();

        if (schemaRetry == null || !Boolean.TRUE.equals(schemaRetry.getEnabled())) {
            return pipeline;
        }

        String strategy = schemaRetry.getStrategy() != null
                ? schemaRetry.getStrategy().toLowerCase()
                : "backoff";

        Retry retrySpec = buildRetrySpec(strategy);

        // Simple: retry on any exception when enabled
        return pipeline.retryWhen(retrySpec);
    }

    private Retry buildRetrySpec(String strategy) {
        Duration initial = Duration.ofMillis(retryProperties.getInitialDelayMs());
        int attempts = retryProperties.getMaxAttempts();

        return switch (strategy) {
            case "fixed" -> Retry.fixedDelay(attempts, initial);
            case "jitter" -> Retry
                    .backoff(attempts, initial)
                    .jitter(0.5);
//            case "backoff":
            default -> Retry.backoff(attempts, initial);
        };
    }
}

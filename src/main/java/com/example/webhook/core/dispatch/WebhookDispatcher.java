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

    public WebhookDispatcher(ReactiveChainBuilder chainBuilder, RetryProperties retryProperties) {
        this.chainBuilder = chainBuilder;
        this.retryProperties = retryProperties;
    }

    /**
     * Execute a full reactive chain. Per-schema retry:
     * - If schema.retry.enabled == true, choose strategy (fixed, backoff, jitter).
     * - If missing or false, no retry.
     */
    public <T> Mono<String> dispatch(WebhookRequestV1<T> request) {
        String schema = request.data().attributes().schema();
        Object input = request.data().attributes().payload();

        GenericReactiveHandlerChain chain = chainBuilder.buildChain(schema);
        WebhookSchemaProperties.SchemaMapping mapping = chainBuilder.getSchemaMapping(schema);

        Mono<String> pipeline = chain.execute(input)
                .map(result -> (String) result);

        WebhookSchemaProperties.SchemaRetry schemaRetry = mapping.getRetry();
        boolean retryEnabled = schemaRetry != null &&
                Boolean.TRUE.equals(schemaRetry.getEnabled());

        if (!retryEnabled) {
            return pipeline;
        }

        String strategy = (schemaRetry.getStrategy() != null)
                ? schemaRetry.getStrategy().toLowerCase()
                : "backoff";

        Retry retrySpec = null;

        /*switch (strategy) {
            case "fixed" -> {
                retrySpec = Retry.fixedDelay(
                        retryProperties.getMaxAttempts(),
                        Duration.ofMillis(retryProperties.getInitialDelayMs())
                );
            }
            case "jitter" -> {
                retrySpec = Retry.backoff(
                                retryProperties.getMaxAttempts(),
                                Duration.ofMillis(retryProperties.getInitialDelayMs()))
                        .jitter(0.5); // 50% jitter
            }
            case "backoff":
            default -> {
                retrySpec = Retry.backoff(
                        retryProperties.getMaxAttempts(),
                        Duration.ofMillis(retryProperties.getInitialDelayMs()));
            }
        }*/

        switch (strategy) {
           /* case "fixed" -> {
                retrySpec = Retry.fixedDelay(
                        retryProperties.getMaxAttempts(),
                        Duration.ofMillis(retryProperties.getInitialDelayMs())
                );
            }*/
            case "jitter" -> {
                retrySpec = Retry.backoff(
                                retryProperties.getMaxAttempts(),
                                Duration.ofMillis(retryProperties.getInitialDelayMs()))
                        .jitter(0.5);
            }
            case "backoff" -> {
                retrySpec = Retry.backoff(
                        retryProperties.getMaxAttempts(),
                        Duration.ofMillis(retryProperties.getInitialDelayMs()));
            }
            default -> {
                retrySpec = Retry.fixedDelay(
                        retryProperties.getMaxAttempts(),
                        Duration.ofMillis(retryProperties.getInitialDelayMs())
                );
            }
        }


//        assert retrySpec != null;
        return pipeline.retryWhen(retrySpec);
    }
}

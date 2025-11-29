package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.integrations.ModuleCClient;
import com.example.webhook.model.payload.WebhookProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("callModuleCHandler")
public class CallModuleCHandler implements ReactiveWebhookHandler<String, WebhookProcessingResult> {

    private static final Logger log = LoggerFactory.getLogger(CallModuleCHandler.class);
    private final ModuleCClient moduleCClient;

    public CallModuleCHandler(ModuleCClient moduleCClient) {
        this.moduleCClient = moduleCClient;
    }

    @Override
    public Mono<WebhookProcessingResult> handle(String ns3Location) {
        if (ns3Location == null || ns3Location.isBlank()) {
            return Mono.error(new ModuleCException("ns3Location is empty, cannot call Module C"));
        }

        log.info("[CallModuleCHandler] Calling Module C with {}", ns3Location);

        return moduleCClient.initiateProcessing(ns3Location)
                .doOnError(ex -> log.error("[CallModuleCHandler] Module C call failed: {}", ex.toString()))
                .onErrorMap(ex -> new ModuleCException("Module C processing failed", ex))
                .map(status -> new WebhookProcessingResult(ns3Location, status));
    }

    public static class ModuleCException extends RuntimeException {
        public ModuleCException(String msg) { super(msg); }
        public ModuleCException(String msg, Throwable cause) { super(msg, cause); }
    }
}

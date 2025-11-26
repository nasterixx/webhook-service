package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.integrations.ModuleCClient;
import com.example.webhook.model.payload.WebhookProcessingResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("callModuleCHandler")
public class CallModuleCHandler implements ReactiveWebhookHandler<String, WebhookProcessingResult> {

    private final ModuleCClient moduleCClient;

    public CallModuleCHandler(ModuleCClient moduleCClient) {
        this.moduleCClient = moduleCClient;
    }

    @Override
    public Mono<WebhookProcessingResult> handle(String ns3Location) {
        return moduleCClient.initiateProcessing(ns3Location)
                .map(status -> new WebhookProcessingResult(ns3Location, status));
    }
}

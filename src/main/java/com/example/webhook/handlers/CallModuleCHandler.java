package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.integrations.ModuleCClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("callModuleCHandler")
public class CallModuleCHandler implements ReactiveWebhookHandler<String, String> {

    private final ModuleCClient moduleCClient;

    public CallModuleCHandler(ModuleCClient moduleCClient) {
        this.moduleCClient = moduleCClient;
    }

    @Override
    public Mono<String> handle(String ns3Location) {
        return moduleCClient.initiateProcessing(ns3Location);
    }
}

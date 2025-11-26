package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("fetchPdfHandler")
public class FetchPdfHandler implements ReactiveWebhookHandler<Map<String, Object>, byte[]> {

    private final WebClient webClient;

    public FetchPdfHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<byte[]> handle(Map<String, Object> payload) {
        Object filenameObj = payload.get("filename");
        if (filenameObj == null || filenameObj.toString().isBlank()) {
            return Mono.error(new IllegalArgumentException("filename is required to fetch PDF"));
        }

        String url = filenameObj.toString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}

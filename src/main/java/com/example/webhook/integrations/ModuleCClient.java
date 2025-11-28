package com.example.webhook.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ModuleCClient {

    private static final Logger log = LoggerFactory.getLogger(ModuleCClient.class);

    private final WebClient webClient;

    public ModuleCClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> initiateProcessing(String ns3Location) {
        log.info("Calling Module C with ns3Location={}", ns3Location);
//        return webClient.post()
//                .uri("https://example.com/module-c/process")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue("{\"ns3Location\":\"" + ns3Location + "\"}")
//                .retrieve()
//                .bodyToMono(String.class);

        return Mono.just("OK");
    }
}

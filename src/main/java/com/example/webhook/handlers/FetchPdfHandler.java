package com.example.webhook.handlers;

import com.example.webhook.core.chain.ReactiveWebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("fetchPdfHandler")
public class FetchPdfHandler implements ReactiveWebhookHandler<Map<String, Object>, byte[]> {

    private static final Logger log = LoggerFactory.getLogger(FetchPdfHandler.class);
    private final WebClient webClient;

    public FetchPdfHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<byte[]> handle(Map<String, Object> payload) {
        Object filenameObj = payload.get("filename");

        if (filenameObj == null || filenameObj.toString().isBlank()) {
            return Mono.error(new PdfFetchException("Missing `filename` in payload"));
        }

        String url = filenameObj.toString();
        log.info("[FetchPdfHandler] Fetching PDF from {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("[FetchPdfHandler] Error response {} : {}", response.statusCode(), body);
                                    return Mono.error(new PdfFetchException(
                                            "Failed to fetch PDF: HTTP " + response.statusCode()));
                                })
                )
                .bodyToMono(byte[].class)
                .doOnError(ex -> {
                    if (ex instanceof WebClientResponseException wcre) {
                        log.error("[FetchPdfHandler] HTTP error {} for {}",
                                wcre.getStatusCode(), url);
                    } else {
                        log.error("[FetchPdfHandler] Unexpected error fetching {}: {}", url, ex.toString());
                    }
                });
    }

    public static class PdfFetchException extends RuntimeException {
        public PdfFetchException(String msg) { super(msg); }
    }
}

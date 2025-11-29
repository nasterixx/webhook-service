package com.example.webhook.security;

import com.example.webhook.config.WebhookSecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class WebhookAuthFilter implements WebFilter {

    private final WebhookSecurityProperties securityProperties;

    public WebhookAuthFilter(WebhookSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        var headers = exchange.getRequest().getHeaders();

        if (securityProperties.getRequiredHeaders() != null) {
            for (String required : securityProperties.getRequiredHeaders()) {
                if (!StringUtils.hasText(headers.getFirst(required))) {
                    return reject(exchange, HttpStatus.BAD_REQUEST, "Missing required header: " + required);
                }
            }
        }

        String token = headers.getFirst("X-Auth-Token");
        if (!StringUtils.hasText(securityProperties.getExpectedToken())
                || !securityProperties.getExpectedToken().equals(token)) {
            return reject(exchange, HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}

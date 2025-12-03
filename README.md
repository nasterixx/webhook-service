# Webhook Service (WebFlux + Chain + Retry + Correlation IDs + Async Callback + Circuit Breakers)

Java 21 + Spring Boot 3 + WebFlux + Swagger + Gradle

- Endpoint: `/api/{version}/webhook`
- Request body: `@RequestBody Mono<WebhookRequestV1>`
- `payload` is `Map<String,Object>` → supports dynamic schemas
- Handler chain per `contentSchema` (from `application.yml`)
- Per-schema retry configuration using Reactor `Retry`
- Correlation IDs via `X-Request-Id` header + Reactor Context + MDC
- Local error handling in controller (no `@RestControllerAdvice`)
- Async fire-and-forget processing with optional callback webhook:
  - Put `callbackUrl` inside `payload` to receive completion status
- Circuit breakers (Resilience4j):
  - `fetchPdf` for PDF download failures/latency
  - `moduleC` for Module C downstream failures/latency

## Run

```bash
cd webhook-service
gradle wrapper
./gradlew bootRun

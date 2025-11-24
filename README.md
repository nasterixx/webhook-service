# Webhook Service (Reactive WebFlux + Dynamic Chain + Per-Schema Retry Strategy)

Java 21 + Spring Boot 3 + WebFlux + Swagger + Gradle (Kotlin DSL)

## Highlights

- **Reactive WebFlux**: non-blocking I/O using `WebClient`
- **Single generic controller**: `/api/{version}/webhook`
- **Dynamic mapping**: `contentSchema` → payload class & handler chain via `application.yml`
- **Reactive Chain of Responsibility**:
  1. `PurchaseCreatedPayload` → `byte[]` (fetch PDF)
  2. `byte[]` → `String` (NS3 path)
  3. `String` → `String` (status from Module C)
- **Global retry tuning**: `webhook.retry.max-attempts`, `initial-delay-ms`, `multiplier`
- **Per-schema retry control**:
  - `retry.enabled`: true/false (optional; missing = no retry)
  - `retry.strategy`: `fixed`, `backoff`, or `jitter` (optional; default = `backoff` if enabled)
- **Security filter**: validates token + required headers for `/api/**`
- **Swagger UI**: `/swagger-ui/index.html`

## Run

```bash
cd webhook-service
gradle wrapper
./gradlew bootRun

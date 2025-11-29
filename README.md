# Webhook Service (Track A + ACK Receiver)

Tech stack:
- Java 21
- Spring Boot 3 (WebFlux)
- Reactor (async)
- Chain of Responsibility
- Dynamic schema-based handler chains
- In-memory queue + background workers (Track A)
- Callback webhook (SUCCESS/ERROR)
- ACK receiver endpoint for testing
- Correlation IDs
- Global error handler
- Gradle (Kotlin DSL)

## Endpoints

### 1. Webhook Receiver

- `POST /api/v1/webhook`
- Body: `WebhookRequestV1` JSON

Example:

```json
{
  "data": {
    "type": "message",
    "attributes": {
      "id": "abc-123",
      "name": "purchase.document",
      "occuredOn": "2025-01-01T10:45:00Z",
      "contentType": "application/json",
      "contentSchema": "c.s.v1.p.cr",
      "additonalContext": {
        "sourceSystem": "SYS_A",
        "region": "EU",
        "type": "purchase",
        "pkg": "PKG_EU1",
        "source": "https://example.com"
      },
      "payload": {
        "filename": "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        "callbackUrl": "http://localhost:8080/api/ack",
        "pId": "PID-1",
        "analysisId": "AN-1"
      }
    }
  }
}

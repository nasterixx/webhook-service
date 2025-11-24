package com.example.webhook.model.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderCreatedPayload extends Payload {

    private String orderId;
    private String orderName;
    private String filename;

}

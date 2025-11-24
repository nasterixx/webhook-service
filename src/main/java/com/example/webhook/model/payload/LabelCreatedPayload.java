package com.example.webhook.model.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LabelCreatedPayload extends Payload {

    private String labelId;
    private String shipperName;
    private String filename;

}

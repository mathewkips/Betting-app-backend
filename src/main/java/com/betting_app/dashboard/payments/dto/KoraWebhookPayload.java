package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;
import java.util.Map;

public record KoraWebhookPayload(
        String event,
        Data data
) {
    public record Data(
            String reference,
            String payment_reference,
            String status,
            BigDecimal amount,
            String currency,
            String message,
            String transaction_reference,
            Map<String, Object> metadata
    ) {
    }
}
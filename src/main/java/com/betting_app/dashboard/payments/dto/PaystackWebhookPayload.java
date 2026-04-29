//package com.betting_app.dashboard.payments.dto;
//
//public class PaystackWebhookPayload {
//
//}

package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PaystackWebhookPayload(
        String event,
        Data data
) {
    public record Data(
            Long id,
            String status,
            String reference,
            BigDecimal amount,
            String currency,
            String gateway_response,
            String paid_at,
            String channel,
            Map<String, Object> metadata
    ) {}
}

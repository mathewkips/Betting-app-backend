package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record KoraChargeInitializeRequest(
        BigDecimal amount,
        String currency,
        String reference,
        String redirect_url,
        String notification_url,
        String narration,
        List<String> channels,
        String default_channel,
        Map<String, Object> metadata,
        Customer customer
) {
    public record Customer(
            String email,
            String name
    ) {
    }
}
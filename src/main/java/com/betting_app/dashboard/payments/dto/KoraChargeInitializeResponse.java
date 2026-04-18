package com.betting_app.dashboard.payments.dto;

public record KoraChargeInitializeResponse(
        boolean status,
        String message,
        Data data
) {
    public record Data(
            String reference,
            String checkout_url
    ) {
    }
}
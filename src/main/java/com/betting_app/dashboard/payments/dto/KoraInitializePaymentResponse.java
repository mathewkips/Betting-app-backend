package com.betting_app.dashboard.payments.dto;

public record KoraInitializePaymentResponse(
        boolean success,
        String message,
        String reference,
        String checkoutUrl
) {
}
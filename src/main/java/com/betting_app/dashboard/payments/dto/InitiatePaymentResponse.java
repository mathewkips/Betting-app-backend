package com.betting_app.dashboard.payments.dto;

public record InitiatePaymentResponse(
        boolean success,
        String message,
        String externalReference
) {
}
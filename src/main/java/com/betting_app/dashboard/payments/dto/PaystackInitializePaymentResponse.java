package com.betting_app.dashboard.payments.dto;

public record PaystackInitializePaymentResponse(
        boolean success,
        String message,
        String reference,
        String authorizationUrl
) {}
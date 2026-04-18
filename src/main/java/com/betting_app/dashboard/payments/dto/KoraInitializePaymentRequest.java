package com.betting_app.dashboard.payments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record KoraInitializePaymentRequest(
        @NotBlank String planName,
        @NotBlank String phone,
        @NotBlank @Email String email
) {
}
package com.betting_app.dashboard.payments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PaystackInitializePaymentRequest(
        @NotBlank String planName,
        //@NotBlank @Email String email,
        @NotBlank String phone
) {}
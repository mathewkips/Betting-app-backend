package com.betting_app.dashboard.tips.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.betting_app.dashboard.common.enums.TipStatus;

public record UpdateTipRequest(
        @NotBlank String title,
        @NotBlank String matchName,
        @NotBlank String league,
        @NotBlank String prediction,
        @NotBlank String odds,
        String analysis,
        @NotNull Boolean premium,
        @NotNull TipStatus status,
        @NotNull LocalDateTime kickoffTime,
        @NotNull Boolean published
) {
}
package com.betting_app.dashboard.tips.dto;

import java.time.LocalDateTime;

import com.betting_app.dashboard.common.enums.TipStatus;

public record TipResponse(
        Long id,
        String title,
        String matchName,
        String league,
        String prediction,
        String odds,
        String analysis,
        Boolean premium,
        TipStatus status,
        LocalDateTime kickoffTime,
        Boolean published
) {
}
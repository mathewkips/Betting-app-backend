package com.betting_app.dashboard.tips.dto;

import com.betting_app.dashboard.common.enums.TipStatus;
import java.time.LocalDateTime;

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
) {}
package com.betting_app.dashboard.payments.dto;

import com.betting_app.dashboard.payments.model.Subscription;
import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        String planName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdAt
) {
    public static SubscriptionResponse from(Subscription sub) {
        return new SubscriptionResponse(
                sub.getId(),
                sub.getPlanName(),
                sub.getStatus().name(),
                sub.getStartTime(),
                sub.getEndTime(),
                sub.getCreatedAt()
        );
    }
}
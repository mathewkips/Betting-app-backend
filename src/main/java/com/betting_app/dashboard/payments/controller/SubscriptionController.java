package com.betting_app.dashboard.payments.controller;

import com.betting_app.dashboard.payments.dto.SubscriptionResponse;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> mySubscriptions(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Unauthorized"));
        }

        return ResponseEntity.ok(
                subscriptionRepository.findByUserIdOrderByCreatedAtDesc(authentication.getName())
                        .stream()
                        .map(SubscriptionResponse::from)
                        .toList()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<?> activeSubscription(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Unauthorized"));
        }

        var active = subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(
                        authentication.getName(),
                        SubscriptionStatus.ACTIVE
                )
                .filter(sub -> sub.getEndTime().isAfter(LocalDateTime.now()))
                .map(SubscriptionResponse::from)
                .orElse(null);

        return ResponseEntity.ok(active);
    }
}
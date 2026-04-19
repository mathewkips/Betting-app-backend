package com.betting_app.dashboard.payments.controller;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> mySubscriptions(Authentication authentication) {
        return ResponseEntity.ok(
                subscriptionRepository.findByUserIdOrderByCreatedAtDesc(authentication.getName())
        );
    }
}
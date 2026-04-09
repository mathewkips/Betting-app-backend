
package com.betting_app.dashboard.tips.controller;

import com.betting_app.dashboard.common.enums.TipStatus;
import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.model.Tip;
import com.betting_app.dashboard.tips.repository.TipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/tips")
public class UserTipController {

    private final TipRepository tipRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserTipController(TipRepository tipRepository,
                             SubscriptionRepository subscriptionRepository) {
        this.tipRepository = tipRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<TipResponse>> getTips(Authentication authentication) {
        Optional<Subscription> subscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(authentication.getName(), SubscriptionStatus.ACTIVE);

        boolean hasActiveSubscription = subscription.isPresent()
                && subscription.get().getEndTime() != null
                && subscription.get().getEndTime().isAfter(LocalDateTime.now());

        List<Tip> tips = tipRepository.findAll().stream()
                .filter(tip -> Boolean.TRUE.equals(tip.getPublished()))
                .filter(tip -> tip.getStatus() == TipStatus.WON
                        || tip.getStatus() == TipStatus.LOST
                        || tip.getStatus() == TipStatus.PENDING)
                .filter(tip -> hasActiveSubscription || !Boolean.TRUE.equals(tip.getPremium()))
                .toList();

        List<TipResponse> response = tips.stream()
                .map(tip -> new TipResponse(
                        tip.getId(),
                        tip.getTitle(),
                        tip.getMatchName(),
                        tip.getLeague(),
                        tip.getPrediction(),
                        tip.getOdds(),
                        tip.getAnalysis(),
                        tip.getPremium(),
                        tip.getStatus(),
                        tip.getKickoffTime(),
                        tip.getPublished()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
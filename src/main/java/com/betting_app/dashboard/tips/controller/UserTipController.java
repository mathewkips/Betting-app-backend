package com.betting_app.dashboard.tips.controller;

import com.betting_app.dashboard.payments.service.SubscriptionService;
import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.model.Tip;
import com.betting_app.dashboard.tips.repository.TipRepository;
import com.betting_app.dashboard.user.model.User;
import com.betting_app.dashboard.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/tips")
public class UserTipController {

    private final TipRepository tipRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    public UserTipController(
            TipRepository tipRepository,
            UserRepository userRepository,
            SubscriptionService subscriptionService
    ) {
        this.tipRepository = tipRepository;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<TipResponse>> getTips(Authentication authentication) {
        String username = authentication.getName();

        subscriptionService.expireSubscriptionIfNeeded(username);

        User user = userRepository.findByUsername(username).orElseThrow();
        boolean premiumUser = user.isPremium();

        List<Tip> tips = tipRepository.findByPublishedTrueOrderByKickoffTimeDesc()
                .stream()
                .filter(tip -> premiumUser || !Boolean.TRUE.equals(tip.getPremium()))
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
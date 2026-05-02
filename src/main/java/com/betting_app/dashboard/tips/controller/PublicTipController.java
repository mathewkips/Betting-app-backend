//
//package com.betting_app.dashboard.tips.controller;
//
//import com.betting_app.dashboard.payments.service.SubscriptionService;
//import com.betting_app.dashboard.tips.dto.TipResponse;
//import com.betting_app.dashboard.tips.repository.TipRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/tips")
//public class PublicTipController {
//
//    private final TipRepository tipRepository;
//    private final SubscriptionService subscriptionService;
//
//    public PublicTipController(
//            TipRepository tipRepository,
//            SubscriptionService subscriptionService
//    ) {
//        this.tipRepository = tipRepository;
//        this.subscriptionService = subscriptionService;
//    }
//
////    @GetMapping("/free")
////    public ResponseEntity<List<TipResponse>> getFreeTips() {
////        return ResponseEntity.ok(
////                tipRepository.findByPublishedTrueAndPremiumFalseOrderByKickoffTimeDesc()
////                        .stream()
////                        .map(this::toResponse)
////                        .toList()
////        );
////    }
//    
//    @GetMapping("/free")
//    public ResponseEntity<List<TipResponse>> getFreeTips() {
//        return ResponseEntity.ok(
//                tipRepository.findByPublishedTrueAndPremiumFalseAndArchivedFalseOrderByKickoffTimeDesc()
//                        .stream()
//                        .map(this::toResponse)
//                        .toList()
//        );
//    }
////
////    @GetMapping("/premium")
////    public ResponseEntity<List<TipResponse>> getPremiumTips(Authentication authentication) {
////        String userId = authentication.getName();
////
////        if (!subscriptionService.hasActiveSubscription(userId)) {
////            throw new RuntimeException("Premium subscription required");
////        }
////
////        return ResponseEntity.ok(
////                tipRepository.findByPublishedTrueAndPremiumTrueOrderByKickoffTimeDesc()
////                        .stream()
////                        .map(this::toResponse)
////                        .toList()
////        );
////    }
//    
//    @GetMapping("/premium")
//    public ResponseEntity<List<TipResponse>> getPremiumTips(Authentication authentication) {
//        String userId = authentication.getName();
//
//        if (!subscriptionService.hasActiveSubscription(userId)) {
//            throw new RuntimeException("Premium subscription required");
//        }
//
//        return ResponseEntity.ok(
//                tipRepository.findByPublishedTrueAndPremiumTrueAndArchivedFalseOrderByKickoffTimeDesc()
//                        .stream()
//                        .map(this::toResponse)
//                        .toList()
//        );
//    }
//    
//    @GetMapping("/results")
//    public ResponseEntity<List<TipResponse>> getResults() {
//        return ResponseEntity.ok(
//                tipRepository.findByPublishedTrueOrderByKickoffTimeDesc()
//                        .stream()
//                        .map(this::toResponse)
//                        .toList()
//        );
//    }
//
//    private TipResponse toResponse(com.betting_app.dashboard.tips.model.Tip tip) {
//        return new TipResponse(
//                tip.getId(),
//                tip.getTitle(),
//                tip.getMatchName(),
//                tip.getLeague(),
//                tip.getPrediction(),
//                tip.getOdds(),
//                tip.getAnalysis(),
//                tip.getPremium(),
//                tip.getStatus(),
//                tip.getKickoffTime(),
//                tip.getPublished()
//        );
//    }
//}

package com.betting_app.dashboard.tips.controller;

import com.betting_app.dashboard.common.enums.TipStatus;
import com.betting_app.dashboard.payments.service.SubscriptionService;
import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.model.Tip;
import com.betting_app.dashboard.tips.repository.TipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tips")
public class PublicTipController {

    private final TipRepository tipRepository;
    private final SubscriptionService subscriptionService;

    public PublicTipController(
            TipRepository tipRepository,
            SubscriptionService subscriptionService
    ) {
        this.tipRepository = tipRepository;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/free")
    public ResponseEntity<List<TipResponse>> getFreeTips() {
        return ResponseEntity.ok(
                tipRepository
                        .findByPublishedTrueAndPremiumFalseAndArchivedFalseAndStatusOrderByKickoffTimeDesc(TipStatus.PENDING)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/premium")
    public ResponseEntity<List<TipResponse>> getPremiumTips(Authentication authentication) {
        String userId = authentication.getName();

        if (!subscriptionService.hasActiveSubscription(userId)) {
            throw new RuntimeException("Premium subscription required");
        }

        return ResponseEntity.ok(
                tipRepository
                        .findByPublishedTrueAndPremiumTrueAndArchivedFalseAndStatusOrderByKickoffTimeDesc(TipStatus.PENDING)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/results")
    public ResponseEntity<List<TipResponse>> getResults() {
        return ResponseEntity.ok(
                tipRepository
                        .findByPublishedTrueAndStatusNotOrderByKickoffTimeDesc(TipStatus.PENDING)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private TipResponse toResponse(Tip tip) {
        return new TipResponse(
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
        );
    }
}
package com.betting_app.dashboard.tips.controller;

import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.repository.TipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tips")
public class PublicTipController {

    private final TipRepository tipRepository;

    public PublicTipController(TipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    @GetMapping("/free")
    public ResponseEntity<List<TipResponse>> getFreeTips() {
        List<TipResponse> response = tipRepository
                .findByPublishedTrueAndPremiumFalseOrderByKickoffTimeDesc()
                .stream()
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
package com.betting_app.dashboard.tips.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.repository.TipRepository;

@RestController
@RequestMapping("/api/tips")
public class PublicTipController {

    private final TipRepository tipRepository;

    public PublicTipController(TipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    @GetMapping("/free")
    public ResponseEntity<List<TipResponse>> getFreeTips() {
        List<TipResponse> response = tipRepository.findByPublishedTrueOrderByKickoffTimeDesc()
                .stream()
                .filter(tip -> !Boolean.TRUE.equals(tip.getPremium()))
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
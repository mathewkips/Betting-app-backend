package com.betting_app.dashboard.tips.service;

import com.betting_app.dashboard.common.enums.TipStatus;
import com.betting_app.dashboard.common.exception.NotFoundException;
import com.betting_app.dashboard.tips.dto.*;
import com.betting_app.dashboard.tips.model.Tip;
import com.betting_app.dashboard.tips.repository.TipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipService {

    private final TipRepository tipRepository;

    public TipService(TipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    @Transactional(readOnly = true)
    public List<TipResponse> getAll() {
        return tipRepository.findAllByOrderByKickoffTimeDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipResponse getById(Long id) {
        return mapToResponse(findTip(id));
    }

    public TipResponse create(CreateTipRequest request) {
        Tip tip = new Tip();

        tip.setTitle(request.title().trim());
        tip.setMatchName(request.matchName().trim());
        tip.setLeague(request.league().trim());
        tip.setPrediction(request.prediction().trim());
        tip.setOdds(request.odds().trim());
        tip.setAnalysis(request.analysis());
        tip.setPremium(request.premium());
       // tip.setStatus(TipStatus.PENDING);
        tip.setStatus(request.status());
        tip.setKickoffTime(request.kickoffTime());
        tip.setPublished(request.published() == null ? true : request.published());

        return mapToResponse(tipRepository.save(tip));
    }

    public TipResponse update(Long id, UpdateTipRequest request) {
        Tip tip = findTip(id);

        tip.setTitle(request.title().trim());
        tip.setMatchName(request.matchName().trim());
        tip.setLeague(request.league().trim());
        tip.setPrediction(request.prediction().trim());
        tip.setOdds(request.odds().trim());
        tip.setAnalysis(request.analysis());
        tip.setPremium(request.premium());
        tip.setStatus(request.status());
        tip.setKickoffTime(request.kickoffTime());
        tip.setPublished(request.published());

        return mapToResponse(tipRepository.save(tip));
    }

    public void delete(Long id) {
        tipRepository.delete(findTip(id));
    }

    private Tip findTip(Long id) {
        return tipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tip not found"));
    }

    private TipResponse mapToResponse(Tip tip) {
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
package com.betting_app.dashboard.tips.service;

import org.springframework.stereotype.Service;

import com.betting_app.dashboard.common.enums.TipStatus;
import com.betting_app.dashboard.common.exception.NotFoundException;
import com.betting_app.dashboard.tips.dto.CreateTipRequest;
import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.dto.UpdateTipRequest;
import com.betting_app.dashboard.tips.model.Tip;
import com.betting_app.dashboard.tips.repository.TipRepository;

import java.util.List;

@Service
public class TipService {

    private final TipRepository tipRepository;
    

    public TipService(TipRepository tipRepository) {
		super();
		this.tipRepository = tipRepository;
	}

	public List<TipResponse> getAll() {
        return tipRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TipResponse getById(Long id) {
        Tip tip = tipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tip not found"));
        return mapToResponse(tip);
    }

    public TipResponse create(CreateTipRequest request) {
    	Tip tip = new Tip();

    	tip.setTitle(request.title());
    	tip.setMatchName(request.matchName());
    	tip.setLeague(request.league());
    	tip.setPrediction(request.prediction());
    	tip.setOdds(request.odds());
    	tip.setAnalysis(request.analysis());
    	tip.setPremium(request.premium());
    	tip.setStatus(TipStatus.PENDING);
    	tip.setKickoffTime(request.kickoffTime());
    	tip.setPublished(request.published());

        tipRepository.save(tip);
        return mapToResponse(tip);
    }

    public TipResponse update(Long id, UpdateTipRequest request) {
        Tip tip = tipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tip not found"));

        tip.setTitle(request.title());
        tip.setMatchName(request.matchName());
        tip.setLeague(request.league());
        tip.setPrediction(request.prediction());
        tip.setOdds(request.odds());
        tip.setAnalysis(request.analysis());
        tip.setPremium(request.premium());
        tip.setStatus(request.status());
        tip.setKickoffTime(request.kickoffTime());
        tip.setPublished(request.published());

        tipRepository.save(tip);
        return mapToResponse(tip);
    }

    public void delete(Long id) {
        Tip tip = tipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tip not found"));
        tipRepository.delete(tip);
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

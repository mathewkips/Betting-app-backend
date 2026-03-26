package com.betting_app.dashboard.tips.model;

import com.betting_app.dashboard.common.enums.TipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tip {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String matchName;

    @Column(nullable = false)
    private String league;

    @Column(nullable = false)
    private String prediction;

    @Column(nullable = false)
    private String odds;

    @Column(columnDefinition = "TEXT")
    private String analysis;

    @Column(nullable = false)
    private Boolean premium;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipStatus status;

    @Column(nullable = false)
    private LocalDateTime kickoffTime;

    @Column(nullable = false)
    private Boolean published;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}

	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	public String getOdds() {
		return odds;
	}

	public void setOdds(String odds) {
		this.odds = odds;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public Boolean getPremium() {
		return premium;
	}

	public void setPremium(Boolean premium) {
		this.premium = premium;
	}

	public TipStatus getStatus() {
		return status;
	}

	public void setStatus(TipStatus status) {
		this.status = status;
	}

	public LocalDateTime getKickoffTime() {
		return kickoffTime;
	}

	public void setKickoffTime(LocalDateTime kickoffTime) {
		this.kickoffTime = kickoffTime;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
}
package com.betting_app.dashboard.tips.model;

import com.betting_app.dashboard.common.enums.TipStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tips")
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
    private Boolean premium = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipStatus status = TipStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime kickoffTime;

    @Column(nullable = false)
    private Boolean published = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean archived = false;
    

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (premium == null) premium = false;
        if (published == null) published = true;
        if (status == null) status = TipStatus.PENDING;
        if (archived == null) archived = false; 
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMatchName() { return matchName; }
    public String getLeague() { return league; }
    public String getPrediction() { return prediction; }
    public String getOdds() { return odds; }
    public String getAnalysis() { return analysis; }
    public Boolean getPremium() { return premium; }
    public TipStatus getStatus() { return status; }
    public LocalDateTime getKickoffTime() { return kickoffTime; }
    public Boolean getArchived() { return archived; }
    public Boolean getPublished() { return published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMatchName(String matchName) { this.matchName = matchName; }
    public void setLeague(String league) { this.league = league; }
    public void setPrediction(String prediction) { this.prediction = prediction; }
    public void setOdds(String odds) { this.odds = odds; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public void setPremium(Boolean premium) { this.premium = premium; }
    public void setArchived(Boolean archived) { this.archived = archived; }
    public void setStatus(TipStatus status) { this.status = status; }
    public void setKickoffTime(LocalDateTime kickoffTime) { this.kickoffTime = kickoffTime; }
    public void setPublished(Boolean published) { this.published = published; }
}
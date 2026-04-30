package com.betting_app.dashboard.payments.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private String durationText;
    private Integer durationDays;
    private Boolean active = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getPrice() { return price; }
    public String getDurationText() { return durationText; }
    public Integer getDurationDays() { return durationDays; }
    public Boolean getActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Integer price) { this.price = price; }
    public void setDurationText(String durationText) { this.durationText = durationText; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public void setActive(Boolean active) { this.active = active; }
}
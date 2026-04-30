package com.betting_app.dashboard.payments.controller;

import org.springframework.web.bind.annotation.*;

import com.betting_app.dashboard.payments.model.SubscriptionPlan;
import com.betting_app.dashboard.payments.repository.SubscriptionPlanRepository;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions/plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository repository;

    public SubscriptionPlanController(SubscriptionPlanRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SubscriptionPlan> getPlans() {
        return repository.findByActiveTrue();
    }
}
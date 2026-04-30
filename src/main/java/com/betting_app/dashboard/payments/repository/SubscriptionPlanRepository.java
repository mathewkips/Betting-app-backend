package com.betting_app.dashboard.payments.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.betting_app.dashboard.payments.model.SubscriptionPlan;

import java.util.List;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByActiveTrue();
}
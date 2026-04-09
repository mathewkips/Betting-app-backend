package com.betting_app.dashboard.payments.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserIdAndStatusOrderByEndTimeDesc(String userId, SubscriptionStatus status);
}
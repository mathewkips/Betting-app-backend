//package com.betting_app.dashboard.payments.repository;
//
//import com.betting_app.dashboard.payments.model.Subscription;
//import com.betting_app.dashboard.payments.model.SubscriptionStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
//
//    Optional<Subscription> findTopByUserIdAndStatusOrderByEndTimeDesc(
//            String userId,
//            SubscriptionStatus status
//    );
//}

package com.betting_app.dashboard.payments.repository;

import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserIdAndStatusOrderByEndTimeDesc(
            String userId,
            SubscriptionStatus status
    );

    List<Subscription> findByUserIdOrderByCreatedAtDesc(String userId);
}
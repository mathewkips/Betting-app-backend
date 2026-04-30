//package com.betting_app.dashboard.payments.repository;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.betting_app.dashboard.payments.model.SubscriptionPlan;
//
//import java.util.List;
//
//public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
//    List<SubscriptionPlan> findByActiveTrue();
//}
package com.betting_app.dashboard.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.betting_app.dashboard.payments.model.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByActiveTrue();

    Optional<SubscriptionPlan> findByNameIgnoreCaseAndActiveTrue(String name);
}
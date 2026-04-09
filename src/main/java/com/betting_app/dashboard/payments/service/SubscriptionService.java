package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import com.betting_app.dashboard.user.repository.UserRepository;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public Subscription activateSubscription(String userId, String planName, Payment payment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime;

        switch (planName.toUpperCase()) {
            case "DAILY":
                endTime = now.plusDays(1);
                break;
            case "WEEKLY":
                endTime = now.plusWeeks(1);
                break;
            case "MONTHLY":
                endTime = now.plusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported plan: " + planName);
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanName(planName);
        subscription.setStartTime(now);
        subscription.setEndTime(endTime);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPayment(payment);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        userRepository.findByUsername(userId).ifPresent(user -> {
            user.setPremium(true);
            user.setPremiumExpiry(endTime);
            userRepository.save(user);
        });

        updateFirebasePremiumState(userId, endTime);

        return savedSubscription;
    }

    private void updateFirebasePremiumState(String userId, LocalDateTime endTime) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("premium", true);
        payload.put("adsDisabled", true);
        payload.put("premiumExpiry", endTime.toString());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .updateChildrenAsync(payload);
    }
}
package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import com.betting_app.dashboard.user.repository.UserRepository;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Subscription activateSubscription(String username, String planName, Payment payment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = calculateEndTime(planName, now);

        subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(username, SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(existing);
                });

        Subscription subscription = new Subscription();
        subscription.setUserId(username);
        subscription.setPlanName(planName.toUpperCase());
        subscription.setStartTime(now);
        subscription.setEndTime(endTime);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPayment(payment);

        Subscription saved = subscriptionRepository.save(subscription);

        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPremium(true);
            user.setAdsBlocked(true);
            user.setPremiumExpiry(endTime);
            userRepository.save(user);
        });

        //updateFirebasePremiumState(username, true, true, endTime);

        return saved;
    }

    @Transactional
    public void expireSubscriptionIfNeeded(String username) {
        subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(username, SubscriptionStatus.ACTIVE)
                .ifPresent(subscription -> {
                    if (subscription.getEndTime() != null &&
                            !subscription.getEndTime().isAfter(LocalDateTime.now())) {

                        subscription.setStatus(SubscriptionStatus.EXPIRED);
                        subscriptionRepository.save(subscription);

                        userRepository.findByUsername(username).ifPresent(user -> {
                            user.setPremium(false);
                            user.setAdsBlocked(false);
                            user.setPremiumExpiry(subscription.getEndTime());
                            userRepository.save(user);
                        });

                        //updateFirebasePremiumState(username, false, false, subscription.getEndTime());
                    }
                });
    }

    public boolean hasActiveSubscription(String username) {
        return subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(username, SubscriptionStatus.ACTIVE)
                .filter(sub -> sub.getEndTime() != null)
                .filter(sub -> sub.getEndTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }
    
    

    private LocalDateTime calculateEndTime(String planName, LocalDateTime now) {
        return switch (planName.toUpperCase()) {
            case "DAILY" -> now.plusDays(1);
            case "WEEKLY" -> now.plusWeeks(1);
            case "MONTHLY" -> now.plusMonths(1);
            default -> throw new IllegalArgumentException("Unsupported plan: " + planName);
        };
    }

    private void updateFirebasePremiumState(
            String username,
            boolean premium,
            boolean adsDisabled,
            LocalDateTime expiry
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("premium", premium);
        data.put("adsDisabled", adsDisabled);
        data.put("premiumExpiry", expiry == null ? null : expiry.toString());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .updateChildrenAsync(data);
    }
}
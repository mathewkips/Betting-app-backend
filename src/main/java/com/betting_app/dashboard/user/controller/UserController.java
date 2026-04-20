//
//package com.betting_app.dashboard.user.controller;
//
//import com.betting_app.dashboard.payments.model.Subscription;
//import com.betting_app.dashboard.payments.model.SubscriptionStatus;
//import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
//import com.betting_app.dashboard.user.dto.UserResponse;
//import com.betting_app.dashboard.user.model.User;
//import com.betting_app.dashboard.user.repository.UserRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController {
//
//    private final UserRepository userRepository;
//    private final SubscriptionRepository subscriptionRepository;
//
//    public UserController(UserRepository userRepository,
//                          SubscriptionRepository subscriptionRepository) {
//        this.userRepository = userRepository;
//        this.subscriptionRepository = subscriptionRepository;
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> me(Authentication authentication) {
//        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
//
//        UserResponse response = new UserResponse();
//        response.setId(user.getId());
//        response.setUsername(user.getUsername());
//        response.setPhoneNumber(user.getPhoneNumber());
//        response.setPremium(user.isPremium());
//        response.setPremiumExpiry(user.getPremiumExpiry() == null ? null : user.getPremiumExpiry().toString());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/subscription-status")
//    public ResponseEntity<?> subscriptionStatus(Authentication authentication) {
//        Optional<Subscription> subscription = subscriptionRepository
//                .findTopByUserIdAndStatusOrderByEndTimeDesc(authentication.getName(), SubscriptionStatus.ACTIVE);
//
//        return ResponseEntity.ok(subscription);
//    }
//}

package com.betting_app.dashboard.user.controller;

import com.betting_app.dashboard.payments.model.Subscription;
import com.betting_app.dashboard.payments.model.SubscriptionStatus;
import com.betting_app.dashboard.payments.repository.SubscriptionRepository;
import com.betting_app.dashboard.payments.service.SubscriptionService;
import com.betting_app.dashboard.user.dto.UserResponse;
import com.betting_app.dashboard.user.model.User;
import com.betting_app.dashboard.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    public UserController(
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionService subscriptionService
    ) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        String username = authentication.getName();

        subscriptionService.expireSubscriptionIfNeeded(username);

        User user = userRepository.findByUsername(username).orElseThrow();

        Optional<Subscription> activeSubscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(username, SubscriptionStatus.ACTIVE);

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setPremium(user.isPremium());
        response.setPremiumExpiry(
                user.getPremiumExpiry() == null ? null : user.getPremiumExpiry().toString()
        );
        response.setPlanName(activeSubscription.map(Subscription::getPlanName).orElse(null));
        response.setNotificationsEnabled(false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription-status")
    public ResponseEntity<?> subscriptionStatus(Authentication authentication) {
        Optional<Subscription> subscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndTimeDesc(
                        authentication.getName(),
                        SubscriptionStatus.ACTIVE
                );
        return ResponseEntity.ok(subscription);
    }
}
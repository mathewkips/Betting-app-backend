
package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.dto.PaymentConfigDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentConfigService {

    public PaymentConfigService() {
    }

    public PaymentConfigDto getPaymentConfig() {
        try {
            DataSnapshot snapshot = readOnce("paymentConfig");

            if (!snapshot.exists() || snapshot.getValue() == null) {
                throw new RuntimeException("paymentConfig not found in Firebase");
            }

            Object rawValue = snapshot.getValue();
            if (!(rawValue instanceof Map)) {
                throw new RuntimeException("paymentConfig is not a valid object");
            }

            Map<?, ?> data = (Map<?, ?>) rawValue;
            Object plansObject = data.get("plans");

            if (!(plansObject instanceof Map)) {
                throw new RuntimeException("plans section not found in paymentConfig");
            }

            Map<?, ?> plans = (Map<?, ?>) plansObject;

            PaymentConfigDto dto = new PaymentConfigDto();
            dto.setActiveChannelId(String.valueOf(data.get("activeChannelId")));
            dto.setStkEnabled(Boolean.parseBoolean(String.valueOf(data.get("stkEnabled"))));
            dto.setDailyPrice(new BigDecimal(String.valueOf(plans.get("DAILY"))));
            dto.setWeeklyPrice(new BigDecimal(String.valueOf(plans.get("WEEKLY"))));
            dto.setMonthlyPrice(new BigDecimal(String.valueOf(plans.get("MONTHLY"))));

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read payment config from Firebase", e);
        }
    }

    private DataSnapshot readOnce(String path) throws Exception {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        FirebaseDatabase.getInstance()
                .getReference(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        future.complete(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(
                                new RuntimeException("Firebase error: " + error.getMessage())
                        );
                    }
                });

        return future.get(10, TimeUnit.SECONDS);
    }

    public BigDecimal resolvePlanPrice(PaymentConfigDto config, String plan) {
        if (plan == null || plan.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan must not be null or empty");
        }

        String normalizedPlan = plan.trim().toUpperCase();

        switch (normalizedPlan) {
            case "DAILY":
                return config.getDailyPrice();
            case "WEEKLY":
                return config.getWeeklyPrice();
            case "MONTHLY":
                return config.getMonthlyPrice();
            default:
                throw new IllegalArgumentException("Unsupported plan: " + plan);
        }
    }
}
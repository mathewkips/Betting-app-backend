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

    public PaymentConfigDto getPaymentConfig() {
        try {
            DataSnapshot snapshot = readOnce("paymentConfig");
            if (!snapshot.exists() || snapshot.getValue() == null) {
                throw new RuntimeException("paymentConfig not found in Firebase");
            }

            Object rawValue = snapshot.getValue();
            if (!(rawValue instanceof Map<?, ?> data)) {
                throw new RuntimeException("paymentConfig is not a valid object");
            }

            Object plansObject = data.get("plans");
            if (!(plansObject instanceof Map<?, ?> plans)) {
                throw new RuntimeException("plans section not found in paymentConfig");
            }

            PaymentConfigDto dto = new PaymentConfigDto();
//            dto.setActiveProvider(String.valueOf(data.getOrDefault("activeProvider", "KORA")));
//            dto.setPaymentsEnabled(Boolean.parseBoolean(String.valueOf(data.getOrDefault("paymentsEnabled", true))));
            Object activeProviderValue = data.get("activeProvider");
            Object paymentsEnabledValue = data.get("paymentsEnabled");

            dto.setActiveProvider(
                    activeProviderValue != null ? String.valueOf(activeProviderValue) : "KORA"
            );

            dto.setPaymentsEnabled(
                    paymentsEnabledValue != null && Boolean.parseBoolean(String.valueOf(paymentsEnabledValue))
            );
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

        return switch (plan.trim().toUpperCase()) {
            case "DAILY" -> config.getDailyPrice();
            case "WEEKLY" -> config.getWeeklyPrice();
            case "MONTHLY" -> config.getMonthlyPrice();
            default -> throw new IllegalArgumentException("Unsupported plan: " + plan);
        };
    }
}
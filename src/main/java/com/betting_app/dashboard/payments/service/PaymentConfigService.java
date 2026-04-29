package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.dto.PaymentConfigDto;
import com.google.firebase.database.*;
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
                return defaultConfig();
            }

            Object rawValue = snapshot.getValue();

            if (!(rawValue instanceof Map<?, ?> data)) {
                return defaultConfig();
            }

            Object plansObject = data.get("plans");

            if (!(plansObject instanceof Map<?, ?> plans)) {
                return defaultConfig();
            }

            PaymentConfigDto dto = new PaymentConfigDto();

            dto.setActiveProvider(
                    getString(data, "activeProvider", "PAYSTACK").trim().toUpperCase()
            );

            dto.setPaymentsEnabled(
                    getBoolean(data, "paymentsEnabled", true)
            );

            dto.setDailyPrice(getMoney(plans, "DAILY", "1"));
            dto.setWeeklyPrice(getMoney(plans, "WEEKLY", "2"));
            dto.setMonthlyPrice(getMoney(plans, "MONTHLY", "5"));

            return dto;

        } catch (Exception e) {
            return defaultConfig();
        }
    }

    public BigDecimal resolvePlanPrice(PaymentConfigDto config, String planName) {
        if (config == null) {
            throw new IllegalArgumentException("Payment config is missing");
        }

        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }

        return switch (planName.trim().toUpperCase()) {
            case "DAILY" -> config.getDailyPrice();
            case "WEEKLY" -> config.getWeeklyPrice();
            case "MONTHLY" -> config.getMonthlyPrice();
            default -> throw new IllegalArgumentException("Unsupported plan: " + planName);
        };
    }

    private PaymentConfigDto defaultConfig() {
        PaymentConfigDto dto = new PaymentConfigDto();

        dto.setActiveProvider("PAYSTACK");
        dto.setPaymentsEnabled(true);
        dto.setDailyPrice(BigDecimal.valueOf(1));
        dto.setWeeklyPrice(BigDecimal.valueOf(2));
        dto.setMonthlyPrice(BigDecimal.valueOf(5));

        return dto;
    }

    private String getString(Map<?, ?> data, String key, String fallback) {
        Object value = data.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    private boolean getBoolean(Map<?, ?> data, String key, boolean fallback) {
        Object value = data.get(key);

        if (value == null) {
            return fallback;
        }

        return Boolean.parseBoolean(String.valueOf(value));
    }

    private BigDecimal getMoney(Map<?, ?> data, String key, String fallback) {
        Object value = data.get(key);

        try {
            return new BigDecimal(value == null ? fallback : String.valueOf(value));
        } catch (Exception e) {
            return new BigDecimal(fallback);
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
                                new RuntimeException(error.getMessage())
                        );
                    }
                });

        return future.get(10, TimeUnit.SECONDS);
    }
}
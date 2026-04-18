package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;

public class PaymentConfigDto {
    private String activeProvider;
    private boolean paymentsEnabled;
    private BigDecimal dailyPrice;
    private BigDecimal weeklyPrice;
    private BigDecimal monthlyPrice;

    public PaymentConfigDto() {
    }

    public PaymentConfigDto(
            String activeProvider,
            boolean paymentsEnabled,
            BigDecimal dailyPrice,
            BigDecimal weeklyPrice,
            BigDecimal monthlyPrice
    ) {
        this.activeProvider = activeProvider;
        this.paymentsEnabled = paymentsEnabled;
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
    }

    public String getActiveProvider() {
        return activeProvider;
    }

    public void setActiveProvider(String activeProvider) {
        this.activeProvider = activeProvider;
    }

    public boolean isPaymentsEnabled() {
        return paymentsEnabled;
    }

    public void setPaymentsEnabled(boolean paymentsEnabled) {
        this.paymentsEnabled = paymentsEnabled;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public BigDecimal getWeeklyPrice() {
        return weeklyPrice;
    }

    public void setWeeklyPrice(BigDecimal weeklyPrice) {
        this.weeklyPrice = weeklyPrice;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
}
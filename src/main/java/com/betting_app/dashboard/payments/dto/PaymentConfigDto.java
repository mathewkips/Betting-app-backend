package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;

public class PaymentConfigDto {

    private String activeChannelId;
    private boolean stkEnabled;
    private BigDecimal dailyPrice;
    private BigDecimal weeklyPrice;
    private BigDecimal monthlyPrice;

    public PaymentConfigDto() {
    }

    public PaymentConfigDto(String activeChannelId, boolean stkEnabled, BigDecimal dailyPrice,
                            BigDecimal weeklyPrice, BigDecimal monthlyPrice) {
        this.activeChannelId = activeChannelId;
        this.stkEnabled = stkEnabled;
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
    }

    public String getActiveChannelId() {
        return activeChannelId;
    }

    public void setActiveChannelId(String activeChannelId) {
        this.activeChannelId = activeChannelId;
    }

    public boolean isStkEnabled() {
        return stkEnabled;
    }

    public void setStkEnabled(boolean stkEnabled) {
        this.stkEnabled = stkEnabled;
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

    @Override
    public String toString() {
        return "PaymentConfigDto{" +
                "activeChannelId='" + activeChannelId + '\'' +
                ", stkEnabled=" + stkEnabled +
                ", dailyPrice=" + dailyPrice +
                ", weeklyPrice=" + weeklyPrice +
                ", monthlyPrice=" + monthlyPrice +
                '}';
    }
}
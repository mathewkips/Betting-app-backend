package com.betting_app.dashboard.payments.dto;

import jakarta.validation.constraints.NotBlank;

public class InitiatePaymentRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String plan;

    public InitiatePaymentRequest() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
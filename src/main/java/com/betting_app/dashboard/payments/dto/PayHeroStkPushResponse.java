package com.betting_app.dashboard.payments.dto;

public class PayHeroStkPushResponse {

    private boolean success;
    private String message;
    private String reference;
    private String rawResponse;

    public PayHeroStkPushResponse() {
    }

    public PayHeroStkPushResponse(boolean success, String message, String reference, String rawResponse) {
        this.success = success;
        this.message = message;
        this.reference = reference;
        this.rawResponse = rawResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    @Override
    public String toString() {
        return "PayHeroStkPushResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", reference='" + reference + '\'' +
                ", rawResponse='" + rawResponse + '\'' +
                '}';
    }
}
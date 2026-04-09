package com.betting_app.dashboard.payments.dto;

public class PayHeroTransactionStatusResponse {

    private boolean success;
    private String status;
    private String receiptNumber;
    private String providerTransactionId;
    private String message;
    private String rawResponse;

    public PayHeroTransactionStatusResponse() {
    }

    public PayHeroTransactionStatusResponse(boolean success, String status, String receiptNumber,
                                            String providerTransactionId, String message, String rawResponse) {
        this.success = success;
        this.status = status;
        this.receiptNumber = receiptNumber;
        this.providerTransactionId = providerTransactionId;
        this.message = message;
        this.rawResponse = rawResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    @Override
    public String toString() {
        return "PayHeroTransactionStatusResponse{" +
                "success=" + success +
                ", status='" + status + '\'' +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", providerTransactionId='" + providerTransactionId + '\'' +
                ", message='" + message + '\'' +
                ", rawResponse='" + rawResponse + '\'' +
                '}';
    }
}
package com.betting_app.dashboard.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String channelId;

    @Column(nullable = false, unique = true)
    private String externalReference;

    private String providerTransactionId;

    private String receiptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(length = 1000)
    private String resultMessage;

    private LocalDateTime confirmedAt;

    private LocalDateTime verificationCheckedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    public Payment() {
    }

    public Payment(Long id, String userId, String phone, String planName,
                   BigDecimal amount, String provider, String channelId,
                   String externalReference, String providerTransactionId,
                   String receiptNumber, PaymentStatus status,
                   String resultMessage, LocalDateTime confirmedAt,
                   LocalDateTime verificationCheckedAt,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.planName = planName;
        this.amount = amount;
        this.provider = provider;
        this.channelId = channelId;
        this.externalReference = externalReference;
        this.providerTransactionId = providerTransactionId;
        this.receiptNumber = receiptNumber;
        this.status = status;
        this.resultMessage = resultMessage;
        this.confirmedAt = confirmedAt;
        this.verificationCheckedAt = verificationCheckedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getProviderTransactionId() { return providerTransactionId; }
    public void setProviderTransactionId(String providerTransactionId) { this.providerTransactionId = providerTransactionId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public LocalDateTime getVerificationCheckedAt() { return verificationCheckedAt; }
    public void setVerificationCheckedAt(LocalDateTime verificationCheckedAt) { this.verificationCheckedAt = verificationCheckedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
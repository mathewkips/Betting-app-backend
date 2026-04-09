package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.dto.PayHeroTransactionStatusResponse;
import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.PaymentStatus;
import com.betting_app.dashboard.payments.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentVerificationService {

    private final PaymentRepository paymentRepository;
    private final PayHeroService payHeroService;
    private final SubscriptionService subscriptionService;

    public PaymentVerificationService(
            PaymentRepository paymentRepository,
            PayHeroService payHeroService,
            SubscriptionService subscriptionService
    ) {
        this.paymentRepository = paymentRepository;
        this.payHeroService = payHeroService;
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedDelay = 60000)
    public void verifyPendingPayments() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);
        List<Payment> pendingPayments =
                paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, cutoff);

        for (Payment payment : pendingPayments) {
            verifyOne(payment);
        }
    }

    public void verifyOne(Payment payment) {
        PayHeroTransactionStatusResponse statusResponse =
                payHeroService.getTransactionStatus(payment.getExternalReference());

        payment.setVerificationCheckedAt(LocalDateTime.now());

        String status = safe(statusResponse.getStatus());
        String message = safe(statusResponse.getMessage());

        if ("success".equalsIgnoreCase(status)) {
            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setReceiptNumber(statusResponse.getReceiptNumber());
                payment.setProviderTransactionId(statusResponse.getProviderTransactionId());
                payment.setConfirmedAt(LocalDateTime.now());
                payment.setResultMessage(message.isEmpty() ? "Verified successful payment" : message);
                paymentRepository.save(payment);

                subscriptionService.activateSubscription(
                        payment.getUserId(),
                        payment.getPlanName(),
                        payment
                );
            }
        } else if ("failed".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
            if ("cancelled".equalsIgnoreCase(status)) {
                payment.setStatus(PaymentStatus.CANCELLED);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }

            payment.setResultMessage(message.isEmpty() ? "Verified failed payment" : message);
            paymentRepository.save(payment);
        } else {
            payment.setResultMessage(message.isEmpty() ? "Still pending" : message);
            paymentRepository.save(payment);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
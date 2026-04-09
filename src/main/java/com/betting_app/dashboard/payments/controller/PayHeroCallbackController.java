
package com.betting_app.dashboard.payments.controller;

import com.betting_app.dashboard.payments.dto.PayHeroCallbackRequest;
import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.PaymentStatus;
import com.betting_app.dashboard.payments.repository.PaymentRepository;
import com.betting_app.dashboard.payments.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments/payhero")
public class PayHeroCallbackController {

    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;

    public PayHeroCallbackController(
            PaymentRepository paymentRepository,
            SubscriptionService subscriptionService
    ) {
        this.paymentRepository = paymentRepository;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(
            @RequestBody PayHeroCallbackRequest callback
    ) {
        String externalReference = firstNonNull(
                callback.getString("external_reference"),
                callback.getString("reference"),
                callback.getString("externalReference")
        );

        if (externalReference == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Missing external reference"
            ));
        }

        Optional<Payment> paymentOptional = paymentRepository.findByExternalReference(externalReference);

        if (paymentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Payment not found"
            ));
        }

        Payment payment = paymentOptional.get();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment already processed"
            ));
        }

        String status = firstNonNull(
                callback.getString("status"),
                callback.getString("payment_status"),
                callback.getString("result")
        );

        String receipt = firstNonNull(
                callback.getString("receipt_number"),
                callback.getString("mpesa_receipt"),
                callback.getString("receipt")
        );

        String transactionId = firstNonNull(
                callback.getString("transaction_id"),
                callback.getString("provider_transaction_id"),
                callback.getString("checkout_request_id")
        );

        String message = firstNonNull(
                callback.getString("message"),
                callback.getString("result_message"),
                callback.getString("description")
        );

        if ("success".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setReceiptNumber(receipt);
            payment.setProviderTransactionId(transactionId);
            payment.setConfirmedAt(LocalDateTime.now());
            payment.setResultMessage(message == null ? "Payment successful" : message);
            paymentRepository.save(payment);

            subscriptionService.activateSubscription(
                    payment.getUserId(),
                    payment.getPlanName(),
                    payment
            );

        } else if ("cancelled".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setProviderTransactionId(transactionId);
            payment.setResultMessage(message == null ? "Payment cancelled" : message);
            paymentRepository.save(payment);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setProviderTransactionId(transactionId);
            payment.setResultMessage(message == null ? "Payment failed" : message);
            paymentRepository.save(payment);
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Callback processed"
        ));
    }

    private String firstNonNull(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }
}
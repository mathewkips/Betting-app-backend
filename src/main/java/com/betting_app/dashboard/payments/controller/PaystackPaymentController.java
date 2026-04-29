package com.betting_app.dashboard.payments.controller;

import com.betting_app.dashboard.payments.dto.PaystackInitializePaymentRequest;
import com.betting_app.dashboard.payments.dto.PaystackInitializePaymentResponse;
import com.betting_app.dashboard.payments.service.PaystackPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/paystack")
public class PaystackPaymentController {

    private final PaystackPaymentService paystackPaymentService;

    public PaystackPaymentController(PaystackPaymentService paystackPaymentService) {
        this.paystackPaymentService = paystackPaymentService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<PaystackInitializePaymentResponse> initialize(
            @Valid @RequestBody PaystackInitializePaymentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                paystackPaymentService.initialize(request, authentication)
        );
    }


    @GetMapping("/verify/{reference}")
    public ResponseEntity<Map<String, Object>> verify(
            @PathVariable String reference,
            Authentication authentication
    ) {
        return ResponseEntity.ok(paystackPaymentService.verifyAndActivate(reference, authentication.getName()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature
    ) {
        paystackPaymentService.handleWebhook(rawBody, signature);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> myPayments(Authentication authentication) {
        return ResponseEntity.ok(paystackPaymentService.getPaymentsForUser(authentication.getName()));
    }

    @GetMapping("/status/{reference}")
    public ResponseEntity<?> status(
            @PathVariable String reference,
            Authentication authentication
    ) {
        return ResponseEntity.ok(paystackPaymentService.getPaymentStatus(authentication.getName(), reference));
    }
}
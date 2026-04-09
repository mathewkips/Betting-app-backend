package com.betting_app.dashboard.payments.controller;

import com.betting_app.dashboard.payments.dto.*;
import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.PaymentStatus;
import com.betting_app.dashboard.payments.repository.PaymentRepository;
import com.betting_app.dashboard.payments.service.PayHeroService;
import com.betting_app.dashboard.payments.service.PaymentConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentConfigService paymentConfigService;
    private final PayHeroService payHeroService;
    private final PaymentRepository paymentRepository;

    @Value("${app.callback-base-url:http://localhost:8080}")
    private String callbackBaseUrl;

    public PaymentController(PaymentConfigService paymentConfigService,
                             PayHeroService payHeroService,
                             PaymentRepository paymentRepository) {
        this.paymentConfigService = paymentConfigService;
        this.payHeroService = payHeroService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/stk-push")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        PaymentConfigDto config = paymentConfigService.getPaymentConfig();

        if (!config.isStkEnabled()) {
            return ResponseEntity.badRequest().body(
                    new InitiatePaymentResponse(false, "STK payments are currently disabled", null)
            );
        }

        BigDecimal amount = paymentConfigService.resolvePlanPrice(config, request.getPlan());
        String externalReference = "PAY-" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setUserId(username);
        payment.setPhone(normalizePhone(request.getPhone()));
        payment.setPlanName(request.getPlan().trim().toUpperCase());
        payment.setAmount(amount);
        payment.setProvider("PAYHERO");
        payment.setChannelId(config.getActiveChannelId());
        payment.setExternalReference(externalReference);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setResultMessage("Awaiting callback or verification");

        paymentRepository.save(payment);

        PayHeroStkPushRequest payHeroRequest = new PayHeroStkPushRequest();
        payHeroRequest.setAmount(amount);
        payHeroRequest.setPhone_number(payment.getPhone());
        payHeroRequest.setChannel_id(config.getActiveChannelId());
        payHeroRequest.setProvider("m-pesa");
        payHeroRequest.setExternal_reference(externalReference);
        payHeroRequest.setCallback_url(callbackBaseUrl + "/api/payments/payhero/callback");
        payHeroRequest.setCustomer_name(username);

        PayHeroStkPushResponse response = payHeroService.initiateStkPush(payHeroRequest);

        return ResponseEntity.ok(
                new InitiatePaymentResponse(
                        response.isSuccess(),
                        response.isSuccess() ? "STK push sent successfully" : response.getMessage(),
                        externalReference
                )
        );
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return "";
        }

        String cleaned = phone.replaceAll("\\s+", "");

        if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1);
        }

        if (cleaned.startsWith("+254")) {
            return cleaned.substring(1);
        }

        return cleaned;
    }
}
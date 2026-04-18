package com.betting_app.dashboard.payments.controller;

import com.betting_app.dashboard.payments.dto.KoraInitializePaymentRequest;
import com.betting_app.dashboard.payments.dto.KoraInitializePaymentResponse;
import com.betting_app.dashboard.payments.dto.KoraWebhookPayload;
import com.betting_app.dashboard.payments.service.KoraPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/kora")
public class KoraPaymentController {

    private final KoraPaymentService koraPaymentService;

    public KoraPaymentController(KoraPaymentService koraPaymentService) {
        this.koraPaymentService = koraPaymentService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<KoraInitializePaymentResponse> initialize(
            @Valid @RequestBody KoraInitializePaymentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(koraPaymentService.initialize(request, authentication));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> webhook(
            @RequestBody KoraWebhookPayload payload,
            @RequestHeader(value = "x-korapay-signature", required = false) String signature
    ) {
        if (!koraPaymentService.isValidWebhookSignature(payload, signature)) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Invalid signature"
            ));
        }

        koraPaymentService.handleWebhook(payload);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Webhook processed"
        ));
    }
}
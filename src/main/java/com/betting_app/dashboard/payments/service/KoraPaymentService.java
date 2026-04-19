package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.dto.KoraChargeInitializeRequest;
import com.betting_app.dashboard.payments.dto.KoraChargeInitializeResponse;
import com.betting_app.dashboard.payments.dto.KoraInitializePaymentRequest;
import com.betting_app.dashboard.payments.dto.KoraInitializePaymentResponse;
import com.betting_app.dashboard.payments.dto.KoraWebhookPayload;
import com.betting_app.dashboard.payments.dto.PaymentConfigDto;
import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.PaymentStatus;
import com.betting_app.dashboard.payments.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KoraPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentConfigService paymentConfigService;
    private final SubscriptionService subscriptionService;
    private final RestTemplate restTemplate;
    //private final ObjectMapper objectMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kora.secret-key}")
    private String koraSecretKey;

    @Value("${kora.base-url}")
    private String koraBaseUrl;

    @Value("${app.callback-base-url}")
    private String callbackBaseUrl;

    @Value("${app.frontend-return-url:http://localhost:3000}")
    private String frontendReturnUrl;


    public KoraPaymentService(
            PaymentRepository paymentRepository,
            PaymentConfigService paymentConfigService,
            SubscriptionService subscriptionService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentConfigService = paymentConfigService;
        this.subscriptionService = subscriptionService;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public KoraInitializePaymentResponse initialize(
            KoraInitializePaymentRequest request,
            Authentication authentication
    ) {
        PaymentConfigDto config = paymentConfigService.getPaymentConfig();

        if (!config.isPaymentsEnabled()) {
            throw new RuntimeException("Payments are currently disabled");
        }

        if (!"KORA".equalsIgnoreCase(config.getActiveProvider())) {
            throw new RuntimeException("Active payment provider is not Kora");
        }

        String userId = authentication.getName();
        BigDecimal amount = paymentConfigService.resolvePlanPrice(config, request.planName());
        String reference = "KORA-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPhone(request.phone());
        payment.setPlanName(request.planName().trim().toUpperCase());
        payment.setAmount(amount);
        payment.setProvider("KORA");
        payment.setChannelId("CHECKOUT");
        payment.setExternalReference(reference);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setResultMessage("Kora checkout initialized");
        paymentRepository.save(payment);

        KoraChargeInitializeRequest payload = new KoraChargeInitializeRequest(
                amount,
                "KES",
                reference,
                frontendReturnUrl,
                callbackBaseUrl + "/api/payments/kora/webhook",
                "Subscription payment - " + request.planName(),
                List.of("mobile_money", "card", "bank_transfer"),
                "mobile_money",
                Map.of(
                        "userId", userId,
                        "planName", request.planName().trim().toUpperCase(),
                        "phone", request.phone()
                ),
                new KoraChargeInitializeRequest.Customer(
                        request.email(),
                        userId
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(koraSecretKey);

        HttpEntity<KoraChargeInitializeRequest> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<KoraChargeInitializeResponse> response = restTemplate.exchange(
                koraBaseUrl + "/merchant/api/v1/charges/initialize",
                HttpMethod.POST,
                entity,
                KoraChargeInitializeResponse.class
        );

        KoraChargeInitializeResponse body = response.getBody();

        if (body == null || !body.status() || body.data() == null || body.data().checkout_url() == null) {
            throw new RuntimeException("Failed to initialize Kora payment");
        }

        return new KoraInitializePaymentResponse(
                true,
                body.message(),
                reference,
                body.data().checkout_url()
        );
    }
    
    //Added this now as a fix
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForUser(String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentStatus(String userId, String reference) {
        Payment payment = paymentRepository.findByExternalReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        return Map.of(
                "reference", payment.getExternalReference(),
                "status", payment.getStatus().name(),
                "planName", payment.getPlanName(),
                "amount", payment.getAmount(),
                "confirmedAt", payment.getConfirmedAt()
        );
    }

    @Transactional
    public void handleWebhook(KoraWebhookPayload payload) {
        if (payload == null || payload.data() == null || payload.data().reference() == null) {
            return;
        }

        Payment payment = paymentRepository.findByExternalReference(payload.data().reference())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setVerificationCheckedAt(LocalDateTime.now());
        payment.setProviderTransactionId(payload.data().transaction_reference());
        payment.setReceiptNumber(payload.data().payment_reference());
        payment.setResultMessage(payload.data().message());

        String status = payload.data().status() == null ? "" : payload.data().status().trim().toUpperCase();

        switch (status) {
            case "SUCCESS", "SUCCESSFUL", "COMPLETED" -> {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setConfirmedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                subscriptionService.activateSubscription(
                        payment.getUserId(),
                        payment.getPlanName(),
                        payment
                );
            }
            case "FAILED" -> {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
            case "CANCELLED" -> {
                payment.setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
            }
            default -> paymentRepository.save(payment);
        }
    }

    public boolean isValidWebhookSignature(KoraWebhookPayload payload, String signatureHeader) {
        if (payload == null || payload.data() == null || signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }

        try {
            String dataJson = objectMapper.writeValueAsString(payload.data());
            String generated = hmacSha256Hex(dataJson, koraSecretKey);
            return generated.equalsIgnoreCase(signatureHeader);
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] bytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Kora webhook signature", e);
        }
    }
}
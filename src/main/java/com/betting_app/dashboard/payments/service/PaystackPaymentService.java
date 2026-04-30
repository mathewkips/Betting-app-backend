package com.betting_app.dashboard.payments.service;
import org.springframework.web.client.HttpStatusCodeException;

import com.betting_app.dashboard.payments.dto.*;
import com.betting_app.dashboard.payments.model.*;
import com.betting_app.dashboard.payments.repository.PaymentRepository;
import com.betting_app.dashboard.payments.repository.SubscriptionPlanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaystackPaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionService subscriptionService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.base-url:https://api.paystack.co}")
    private String paystackBaseUrl;

    @Value("${app.frontend-return-url}")
    private String frontendReturnUrl;

    public PaystackPaymentService(
            PaymentRepository paymentRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            SubscriptionService subscriptionService
    ) {
        this.paymentRepository = paymentRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionService = subscriptionService;
    }
    @Transactional
    public PaystackInitializePaymentResponse initialize(
            PaystackInitializePaymentRequest request,
            Authentication authentication
    ) {
        try {
            String userId = authentication.getName();
            SubscriptionPlan plan = subscriptionPlanRepository
                    .findByNameIgnoreCaseAndActiveTrue(request.planName().trim())
                    .orElseThrow(() -> new RuntimeException("Invalid subscription plan"));

            BigDecimal amount = BigDecimal.valueOf(plan.getPrice());
           

            String reference = "PSK-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 18);

            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setPhone(request.phone());
            payment.setPlanName(plan.getName());
           // payment.setPlanName(request.planName().trim().toUpperCase());
            payment.setAmount(amount);
            payment.setProvider("PAYSTACK");
            payment.setChannelId("CHECKOUT");
            payment.setExternalReference(reference);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setResultMessage("Paystack checkout initialized");
            paymentRepository.save(payment);

            Map<String, Object> body = new HashMap<>();
//           // body.put("email", request.email());
            String safePhone = request.phone().replaceAll("[^0-9]", "");
            String email = "user" + safePhone + "@gmail.com"; 
           

            body.put("email", email);
            body.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            body.put("currency", "KES");
            body.put("reference", reference);
            body.put("callback_url", frontendReturnUrl + "/payment/verify?reference=" + reference);
            body.put("metadata", Map.of(
                    "userId", userId,
                    "planName", payment.getPlanName(),
                    "phone", request.phone()
            ));

            ResponseEntity<String> response = restTemplate.exchange(
                    paystackBaseUrl + "/transaction/initialize",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers()),
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data == null || data.path("authorization_url").isMissingNode()) {
                throw new RuntimeException("Failed to initialize Paystack payment");
            }

            return new PaystackInitializePaymentResponse(
                    true,
                    "Paystack checkout initialized",
                    reference,
                    data.path("authorization_url").asText()
            );

        } 
//        catch (Exception e) {
//            throw new RuntimeException("Paystack initialization failed: " + e.getMessage(), e);
        //}
        catch (HttpStatusCodeException e) {
            throw new RuntimeException(
                "Paystack error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                e
            );
        } catch (Exception e) {
            throw new RuntimeException("Paystack initialization failed: " + e.getMessage(), e);
        }
    }


    @Transactional
    public Map<String, Object> verifyAndActivate(String reference, String userId) {
        Payment payment = paymentRepository.findByExternalReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        return verifyReference(reference);
    }

    @Transactional
    public Map<String, Object> verifyReference(String reference) {
        try {
            Payment payment = paymentRepository.findByExternalReference(reference)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            ResponseEntity<String> response = restTemplate.exchange(
                    paystackBaseUrl + "/transaction/verify/" + reference,
                    HttpMethod.GET,
                    new HttpEntity<>(headers()),
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data == null) {
                throw new RuntimeException("Invalid Paystack verification response");
            }

            String paystackStatus = data.path("status").asText("");

            payment.setVerificationCheckedAt(LocalDateTime.now());
            payment.setProviderTransactionId(data.path("id").asText());
            payment.setReceiptNumber(reference);
            payment.setResultMessage(data.path("gateway_response").asText(""));

            if ("success".equalsIgnoreCase(paystackStatus)) {
                if (payment.getStatus() != PaymentStatus.SUCCESS) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setConfirmedAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    subscriptionService.activateSubscription(
                            payment.getUserId(),
                            payment.getPlanName(),
                            payment
                    );
                }
            } else if ("failed".equalsIgnoreCase(paystackStatus)) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

            return Map.of(
                    "success", true,
                    "reference", payment.getExternalReference(),
                    "status", payment.getStatus().name(),
                    "planName", payment.getPlanName(),
                    "amount", payment.getAmount(),
                    "premiumActivated", payment.getStatus() == PaymentStatus.SUCCESS
            );

        } catch (Exception e) {
            throw new RuntimeException("Paystack verification failed: " + e.getMessage(), e);
        }
    }
    @Transactional
    public void handleWebhook(String rawBody, String signature) {
        if (!isValidWebhookSignature(rawBody, signature)) {
            throw new RuntimeException("Invalid Paystack webhook signature");
        }

        try {
            JsonNode root = objectMapper.readTree(rawBody);

            String event = root.path("event").asText();
            String reference = root.path("data").path("reference").asText();

            if ("charge.success".equalsIgnoreCase(event)
                    && reference != null
                    && !reference.isBlank()) {
                verifyReference(reference);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Paystack webhook", e);
        }
    }

    public List<Payment> getPaymentsForUser(String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

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

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackSecretKey);
        return headers;
    }

    private boolean isValidWebhookSignature(String rawBody, String signature) {
        if (rawBody == null || signature == null || signature.isBlank()) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(
                    paystackSecretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            ));

            byte[] hash = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));

            StringBuilder generated = new StringBuilder();
            for (byte b : hash) {
                generated.append(String.format("%02x", b));
            }

            return generated.toString().equalsIgnoreCase(signature);

        } catch (Exception e) {
            return false;
        }
    }
}
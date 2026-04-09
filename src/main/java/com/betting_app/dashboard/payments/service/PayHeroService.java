package com.betting_app.dashboard.payments.service;

import com.betting_app.dashboard.payments.dto.PayHeroStkPushRequest;
import com.betting_app.dashboard.payments.dto.PayHeroStkPushResponse;
import com.betting_app.dashboard.payments.dto.PayHeroTransactionStatusResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PayHeroService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payhero.base-url:https://backend.payhero.co.ke/api/v2}")
    private String payHeroBaseUrl;

    @Value("${payhero.auth-token}")
    private String authToken;

    public PayHeroService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public PayHeroStkPushResponse initiateStkPush(PayHeroStkPushRequest request) {
        String url = payHeroBaseUrl + "/payments";
        HttpHeaders headers = buildHeaders();
        HttpEntity<PayHeroStkPushRequest> entity = new HttpEntity<>(request, headers);

        PayHeroStkPushResponse stkResponse = new PayHeroStkPushResponse();
        stkResponse.setReference(request.getExternal_reference());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            stkResponse.setSuccess(response.getStatusCode().is2xxSuccessful());
            stkResponse.setMessage(response.getBody());
            stkResponse.setRawResponse(response.getBody());
            return stkResponse;

        } catch (HttpStatusCodeException e) {
            stkResponse.setSuccess(false);
            stkResponse.setMessage(e.getResponseBodyAsString());
            stkResponse.setRawResponse(e.getResponseBodyAsString());
            return stkResponse;
        }
    }

    public PayHeroTransactionStatusResponse getTransactionStatus(String externalReference) {
        String url = payHeroBaseUrl + "/transaction-status?reference=" + externalReference;

        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        String raw = response.getBody();
        String status = null;
        String receipt = null;
        String providerTxId = null;
        String message = null;

        try {
            Map<String, Object> body = objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
            status = readFirst(body, "status", "payment_status", "result");
            receipt = readFirst(body, "receipt_number", "mpesa_receipt", "receipt");
            providerTxId = readFirst(body, "transaction_id", "provider_transaction_id", "checkout_request_id");
            message = readFirst(body, "message", "description");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PayHeroTransactionStatusResponse statusResponse = new PayHeroTransactionStatusResponse();
        statusResponse.setSuccess(response.getStatusCode().is2xxSuccessful());
        statusResponse.setStatus(status);
        statusResponse.setReceiptNumber(receipt);
        statusResponse.setProviderTransactionId(providerTxId);
        statusResponse.setMessage(message);
        statusResponse.setRawResponse(raw);
        return statusResponse;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        return headers;
    }

    private String readFirst(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}
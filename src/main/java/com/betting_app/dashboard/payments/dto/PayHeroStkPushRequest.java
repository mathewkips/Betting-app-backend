package com.betting_app.dashboard.payments.dto;

import java.math.BigDecimal;

public class PayHeroStkPushRequest {

    private BigDecimal amount;
    private String phone_number;
    private String channel_id;
    private String provider;
    private String external_reference;
    private String callback_url;
    private String customer_name;

    public PayHeroStkPushRequest() {
    }

    public PayHeroStkPushRequest(BigDecimal amount, String phone_number, String channel_id,
                                 String provider, String external_reference,
                                 String callback_url, String customer_name) {
        this.amount = amount;
        this.phone_number = phone_number;
        this.channel_id = channel_id;
        this.provider = provider;
        this.external_reference = external_reference;
        this.callback_url = callback_url;
        this.customer_name = customer_name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExternal_reference() {
        return external_reference;
    }

    public void setExternal_reference(String external_reference) {
        this.external_reference = external_reference;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    @Override
    public String toString() {
        return "PayHeroStkPushRequest{" +
                "amount=" + amount +
                ", phone_number='" + phone_number + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", provider='" + provider + '\'' +
                ", external_reference='" + external_reference + '\'' +
                ", callback_url='" + callback_url + '\'' +
                ", customer_name='" + customer_name + '\'' +
                '}';
    }
}
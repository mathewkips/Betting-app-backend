package com.betting_app.dashboard.payments.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class PayHeroCallbackRequest {

    private Map<String, Object> fields = new HashMap<>();

    public PayHeroCallbackRequest() {
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        fields.put(key, value);
    }

    public String getString(String key) {
        Object value = fields.get(key);
        return value == null ? null : value.toString();
    }

    @Override
    public String toString() {
        return "PayHeroCallbackRequest{" +
                "fields=" + fields +
                '}';
    }
}

package com.betting_app.dashboard.user.dto;

public class UserResponse {

    private Long id;
    private String username;
    private String phoneNumber;
    private boolean premium;
    private String premiumExpiry;

    public UserResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getPremiumExpiry() {
        return premiumExpiry;
    }

    public void setPremiumExpiry(String premiumExpiry) {
        this.premiumExpiry = premiumExpiry;
    }
}
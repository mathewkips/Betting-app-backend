
package com.betting_app.dashboard.user.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private String token;
    private String username;
    private boolean premium;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, String token, String username, boolean premium) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.username = username;
        this.premium = premium;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
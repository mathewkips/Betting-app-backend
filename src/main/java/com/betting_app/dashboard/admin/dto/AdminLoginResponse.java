package com.betting_app.dashboard.admin.dto;

public record AdminLoginResponse(
        boolean success,
        String message,
        String token,
        String email
) {
}

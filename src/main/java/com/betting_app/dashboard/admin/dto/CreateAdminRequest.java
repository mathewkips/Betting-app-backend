package com.betting_app.dashboard.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(
        @NotBlank String username,
        @NotBlank String fullName,
        @NotBlank
        @Size(min = 4, max = 100)
        String password
) {}
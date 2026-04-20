package com.betting_app.dashboard.admin.controller;

import com.betting_app.dashboard.admin.dto.CreateAdminRequest;
import com.betting_app.dashboard.admin.model.Admin;
import com.betting_app.dashboard.admin.repository.AdminRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminManagementController(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createAdmin(
            @Valid @RequestBody CreateAdminRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "success", false,
                            "message", "Unauthorized"
                    )
            );
        }

        final String email = request.email().trim().toLowerCase();
        final String fullName = request.fullName().trim();

        if (adminRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "success", false,
                            "message", "Admin already exists"
                    )
            );
        }

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setFullName(fullName);
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole("ROLE_ADMIN");

        Admin savedAdmin = adminRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "success", true,
                        "message", "Admin created successfully",
                        "id", savedAdmin.getId(),
                        "email", savedAdmin.getEmail(),
                        "fullName", savedAdmin.getFullName(),
                        "role", savedAdmin.getRole(),
                        "createdBy", authentication.getName(),
                        "createdAt", LocalDateTime.now().toString()
                )
        );
    }
}
package com.betting_app.dashboard.admin.controller;

import com.betting_app.dashboard.admin.dto.CreateAdminRequest;
import com.betting_app.dashboard.admin.model.Admin;
import com.betting_app.dashboard.admin.model.AdminRole;
import com.betting_app.dashboard.admin.repository.AdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(AdminRepository adminRepository,
                                PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAdmins() {
        return ResponseEntity.ok(adminRepository.findAll());
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequest request) {
        String username = request.username().trim();

        if (adminRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setFullName(request.fullName().trim());
        admin.setPassword(passwordEncoder.encode(request.password()));
        //admin.setRole(request.role() == null ? AdminRole.ADMIN : request.role());
        admin.setRole(AdminRole.ADMIN);
        
        adminRepository.save(admin);

        return ResponseEntity.ok("Admin created successfully");
    }

    @DeleteMapping("/admins/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() == AdminRole.SUPER_ADMIN) {
            return ResponseEntity.badRequest().body("Cannot delete SUPER_ADMIN");
        }

        adminRepository.delete(admin);
        return ResponseEntity.ok("Admin deleted successfully");
    }
}
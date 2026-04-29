package com.betting_app.dashboard.config;

import com.betting_app.dashboard.admin.model.Admin;
import com.betting_app.dashboard.admin.model.AdminRole;
import com.betting_app.dashboard.admin.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setFullName("Super Admin");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(AdminRole.SUPER_ADMIN); 
            adminRepository.save(admin);
        }
    }
}
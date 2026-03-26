package com.betting_app.dashboard.init;

import com.betting_app.dashboard.admin.model.Admin;
import com.betting_app.dashboard.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    

    public DataInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		super();
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}


	@Override
    public void run(String... args) {
        //If admin already exists
        if (adminRepository.findByEmail("admin@bettips.com").isEmpty()) {

        	Admin admin = new Admin();
        	admin.setFullName("Main Admin");
        	admin.setEmail("admin@bettips.com");
        	admin.setPassword(passwordEncoder.encode("1234"));
        	admin.setRole("ROLE_ADMIN");

            adminRepository.save(admin);

            System.out.println("Default admin created: admin@bettip.com / 1234");
        } else {
            System.out.println("Admin already exists, skipping initialization");
        }
    }
}
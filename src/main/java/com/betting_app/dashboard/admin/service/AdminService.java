package com.betting_app.dashboard.admin.service;

import com.betting_app.dashboard.admin.model.Admin;
import com.betting_app.dashboard.admin.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        return new User(
                admin.getUsername(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()))
        		);
    }
}
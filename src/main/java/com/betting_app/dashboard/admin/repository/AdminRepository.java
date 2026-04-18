
package com.betting_app.dashboard.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.betting_app.dashboard.admin.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
package com.betting_app.dashboard.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betting_app.dashboard.payments.model.Payment;
import com.betting_app.dashboard.payments.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByExternalReference(String externalReference);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime time);
}
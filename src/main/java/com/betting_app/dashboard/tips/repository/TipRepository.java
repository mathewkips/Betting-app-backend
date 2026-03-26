package com.betting_app.dashboard.tips.repository;

import com.betting_app.dashboard.tips.model.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipRepository extends JpaRepository<Tip, Long> {
}
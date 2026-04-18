
package com.betting_app.dashboard.tips.repository;

import com.betting_app.dashboard.tips.model.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipRepository extends JpaRepository<Tip, Long> {

    List<Tip> findAllByOrderByKickoffTimeDesc();

    List<Tip> findByPublishedTrueOrderByKickoffTimeDesc();
}
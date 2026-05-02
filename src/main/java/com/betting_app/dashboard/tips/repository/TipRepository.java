package com.betting_app.dashboard.tips.repository;

import com.betting_app.dashboard.tips.model.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
//
//public interface TipRepository extends JpaRepository<Tip, Long> {
//    List<Tip> findAllByOrderByKickoffTimeDesc();
//
//    List<Tip> findByPublishedTrueOrderByKickoffTimeDesc();
//
//    List<Tip> findByPublishedTrueAndPremiumFalseOrderByKickoffTimeDesc();
//
//    List<Tip> findByPublishedTrueAndPremiumTrueOrderByKickoffTimeDesc();
//    List<Tip> findByPremiumFalseAndPublishedTrue();
//
//    List<Tip> findByPremiumTrueAndPublishedTrue();
//    
//    List<Tip> findByPublishedTrueAndArchivedFalseOrderByKickoffTimeDesc();
//
//}
public interface TipRepository extends JpaRepository<Tip, Long> {

    List<Tip> findAllByOrderByKickoffTimeDesc();

    // Results screen: show all published, including archived
    List<Tip> findByPublishedTrueOrderByKickoffTimeDesc();

    // Home/free tips: hide archived
    List<Tip> findByPublishedTrueAndPremiumFalseAndArchivedFalseOrderByKickoffTimeDesc();

    // Premium screen: hide archived
    List<Tip> findByPublishedTrueAndPremiumTrueAndArchivedFalseOrderByKickoffTimeDesc();

    // Optional general home query
    List<Tip> findByPublishedTrueAndArchivedFalseOrderByKickoffTimeDesc();
}
//package com.betting_app.dashboard.tips.repository;
//
//import com.betting_app.dashboard.tips.model.Tip;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface TipRepository extends JpaRepository<Tip, Long> {
//
//    List<Tip> findAllByOrderByKickoffTimeDesc();
//
//    // Results screen: show all published, including archived
//    List<Tip> findByPublishedTrueOrderByKickoffTimeDesc();
//
//    // Home/free tips: hide archived
//    List<Tip> findByPublishedTrueAndPremiumFalseAndArchivedFalseOrderByKickoffTimeDesc();
//
//    // Premium screen: hide archived
//    List<Tip> findByPublishedTrueAndPremiumTrueAndArchivedFalseOrderByKickoffTimeDesc();
//
//    // Optional general home query
//    List<Tip> findByPublishedTrueAndArchivedFalseOrderByKickoffTimeDesc();
//}

package com.betting_app.dashboard.tips.repository;

import com.betting_app.dashboard.common.enums.TipStatus;
import com.betting_app.dashboard.tips.model.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipRepository extends JpaRepository<Tip, Long> {

    List<Tip> findAllByOrderByKickoffTimeDesc();

    // Home free tips: only pending
    List<Tip> findByPublishedTrueAndPremiumFalseAndArchivedFalseAndStatusOrderByKickoffTimeDesc(
            TipStatus status
    );

    // Home premium tips: only pending
    List<Tip> findByPublishedTrueAndPremiumTrueAndArchivedFalseAndStatusOrderByKickoffTimeDesc(
            TipStatus status
    );

    // Results: only WON/LOST, no pending
    List<Tip> findByPublishedTrueAndStatusNotOrderByKickoffTimeDesc(
            TipStatus status
    );
    List<Tip> findByPublishedTrueAndArchivedFalseAndStatusOrderByKickoffTimeDesc(
            TipStatus status
    );
}
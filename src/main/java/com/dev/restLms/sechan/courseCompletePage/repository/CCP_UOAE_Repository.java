package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignmentEvaluation;

public interface CCP_UOAE_Repository extends JpaRepository<UserOwnAssignmentEvaluation, String> {
    List<UserOwnAssignmentEvaluation> findByUoaeSessionIdAndAssignmentId(String uoaeSessionId, String assignmentId);
}

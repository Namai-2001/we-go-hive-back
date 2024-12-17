package com.dev.restLms.AssignmentPage.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.AssignmentPage.projection.AssignmentPageUserOwnAssignmentEvaluationProjection;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;

@Repository
public interface AssignmentPageUserOwnAssignmentEvaluationRepository extends JpaRepository<UserOwnAssignmentEvaluation, String> {
  Page<AssignmentPageUserOwnAssignmentEvaluationProjection> findByUoaeSessionId(String sessionId, Pageable pageable);
  Page<AssignmentPageUserOwnAssignmentEvaluationProjection> findByUoaeSessionIdAndIsSubmit(String sessionId, String isSubmit, Pageable pageable);
  Optional<UserOwnAssignmentEvaluation> UoaeSessionIdAndAssignmentId(String sessionId, String assignmentId);
}
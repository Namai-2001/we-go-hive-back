package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignmentEvaluation;

public interface ProcessListUserOwnAssignmentEvaluationRepository extends JpaRepository<UserOwnAssignmentEvaluation, String > {
    
}

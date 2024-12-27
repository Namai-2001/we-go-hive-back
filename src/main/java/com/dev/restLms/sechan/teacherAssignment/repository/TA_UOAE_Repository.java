package com.dev.restLms.sechan.teacherAssignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignmentEvaluation;
import java.util.List;
import java.util.Optional;


public interface TA_UOAE_Repository extends JpaRepository<UserOwnAssignmentEvaluation, String> {
    List<UserOwnAssignmentEvaluation> findByAssignmentIdAndTeacherSessionId(String assignmentId, String teacherSessionId);
    Optional<UserOwnAssignmentEvaluation> findBySubmissionIdAndTeacherSessionId(String submissionId, String teacherSessionId);
}

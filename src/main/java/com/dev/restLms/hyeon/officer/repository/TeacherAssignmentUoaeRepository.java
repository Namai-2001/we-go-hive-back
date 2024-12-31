package com.dev.restLms.hyeon.officer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnAssignmentEvaluation;

@Repository
public interface TeacherAssignmentUoaeRepository extends JpaRepository<UserOwnAssignmentEvaluation, String> {
    List<UserOwnAssignmentEvaluation> findByAssignmentId(String assignmentId);
}
package com.dev.restLms.AssignmentPage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.AssignmentPage.projection.AssignmentPageAssignmentProjection;
import com.dev.restLms.entity.Assignment;

@Repository
public interface AssignmentPageAssignmentRepository extends JpaRepository<Assignment, String>{
  Page<AssignmentPageAssignmentProjection> findByAssignmentIdAndTeacherSessionId(
        String assignmentId, String teacherSessionId, Pageable pageable);
}

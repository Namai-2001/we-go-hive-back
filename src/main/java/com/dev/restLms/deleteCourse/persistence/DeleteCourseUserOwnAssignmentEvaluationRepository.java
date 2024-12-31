package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnAssignmentEvaluation;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;
import java.util.List;


public interface DeleteCourseUserOwnAssignmentEvaluationRepository extends JpaRepository<UserOwnAssignmentEvaluation, String> {
    boolean existsByAssignmentId(String assignmentId);
    void deleteByAssignmentId(String assignmentId);
    List<DeleteCourseUserOwnAssignmentEvaluation> findByAssignmentId(String assignmentId);
}

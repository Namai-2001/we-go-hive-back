package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseAssignment;
import com.dev.restLms.entity.Assignment;
import java.util.List;


public interface DeleteCourseAssignmentRepository extends JpaRepository<Assignment, String> {
    List<DeleteCourseAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
    boolean existsByOfferedSubjectsId(String offeredSubjectsId);
    void deleteByAssignmentId(String assignmentId);
}

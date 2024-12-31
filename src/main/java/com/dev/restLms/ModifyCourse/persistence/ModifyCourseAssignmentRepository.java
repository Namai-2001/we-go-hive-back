package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseAssignment;
import com.dev.restLms.entity.Assignment;
import java.util.List;


public interface ModifyCourseAssignmentRepository extends JpaRepository<Assignment, String> {
    List<ModifyCourseAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
}

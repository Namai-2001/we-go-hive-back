package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnAssignment;
import com.dev.restLms.entity.UserOwnAssignment;
import java.util.List;


public interface DeleteCourseUserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, String> {
    boolean existsByOfferedSubjectsId(String offeredSubjectsId);
    void deleteByOfferedSubjectsId(String offeredSubjectsId);
    List<DeleteCourseUserOwnAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
}

package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseCourse;
import com.dev.restLms.entity.Course;
import java.util.Optional;



public interface ModifyCourseCourseRepository extends JpaRepository<Course, String> {
    Page<ModifyCourseCourse> findBySessionId(String sessionId, Pageable pageable);
    Page<ModifyCourseCourse> findBySessionIdAndCourseTitleContaining(String sessionId, String courseTitle, Pageable pageable);
    Optional<ModifyCourseCourse> findByCourseId(String courseId);
}

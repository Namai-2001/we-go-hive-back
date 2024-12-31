package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourse;
import com.dev.restLms.entity.Course;
import java.util.Optional;


public interface DeleteCourseRepository extends JpaRepository<Course, String> {
    Page<DeleteCourse> findBySessionId(String sessionId, Pageable pageable);
    Page<DeleteCourse> findBySessionIdAndCourseTitleContaining(String sessionId, String courseTitle ,Pageable pageable);
    Optional<DeleteCourse> findByCourseIdAndSessionId(String courseId, String sessionId);
    // boolean existsBySessionId(String sessionId);
}

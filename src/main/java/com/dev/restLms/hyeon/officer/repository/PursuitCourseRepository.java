package com.dev.restLms.hyeon.officer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Course;
import com.dev.restLms.hyeon.officer.projection.PursuitCourse;


@Repository
public interface PursuitCourseRepository extends JpaRepository<Course, String> {
    List<PursuitCourse> findBySessionId(String sessionId);
    List<PursuitCourse> findByCourseIdAndSessionId(String courseId, String sessionId);
}

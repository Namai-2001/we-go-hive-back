package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyCourse;
import com.dev.restLms.entity.Course;
import java.util.List;
import java.util.Optional;


public interface ConductSurveyCourseRepository extends JpaRepository<Course, String> {
    List<ConductSurveyCourse> findBySessionId(String sessionId, Sort sort);
    List<ConductSurveyCourse> findByCourseTitleContainingAndSessionId(String courseTitle, String sessionId, Sort sort);
    Optional<ConductSurveyCourse> findByCourseId(String courseId);
}

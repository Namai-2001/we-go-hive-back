package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;
import java.util.List;


public interface IndividualSurveyStatisticsCourseRepository extends JpaRepository<Course, String> {
    List<Course> findByCourseIdAndSessionId(String courseId, String sessionId, Sort sort);
}

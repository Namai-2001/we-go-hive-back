package com.dev.restLms.SurveyStatistics.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsCourse;
import com.dev.restLms.entity.Course;

@Repository
public interface SurveyStatisticsCourseRepository extends JpaRepository<Course, String>{
    List<SurveyStatisticsCourse> findBySessionId(String sessionId);
}

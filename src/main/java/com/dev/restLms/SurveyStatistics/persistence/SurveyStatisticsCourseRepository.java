package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsCourse;
import com.dev.restLms.entity.Course;
import java.util.List;


@Repository
public interface SurveyStatisticsCourseRepository extends JpaRepository<Course, String>{

    List<SurveyStatisticsCourse> findBySessionId(String sessionId,Sort sort);

    List<Course> findByCourseTitleContainingAndSessionId(String courseTitle, String sessionId, Sort sort);

}

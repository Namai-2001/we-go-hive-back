package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsCourseOwnSubjects;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;


public interface SurveyStatisticsCourseOwnSubjectsRepository extends JpaRepository<CourseOwnSubject, String> {
    List<SurveyStatisticsCourseOwnSubjects> findByCourseIdAndOfficerSessionId(String courseId, String officerSessionId);
}

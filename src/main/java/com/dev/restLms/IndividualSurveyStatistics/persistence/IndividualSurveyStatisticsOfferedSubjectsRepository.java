package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.Optional;


public interface IndividualSurveyStatisticsOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<IndividualSurveyStatisticsOfferedSubjects> findBySubjectIdAndCourseIdAndOfficerSessionId(String subjectId, String courseId, String officerSessionId);
}

package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.Optional;



public interface SurveyStatisticsOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<SurveyStatisticsOfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);

    Optional<SurveyStatisticsOfferedSubjects> findByCourseIdAndOfficerSessionIdAndSubjectId(String courseId, String officerSession, String subjectId);

}

package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyExecution;
import com.dev.restLms.entity.SurveyExecution;
import java.util.Optional;




public interface SurveyStatisticsSurveyExecutionRepository extends JpaRepository<SurveyExecution, String> {
    
    Optional<SurveyStatisticsSurveyExecution> findBySessionIdAndCourseIdAndOfferedSubjectsId(String sessionId, String courseId, String offeredSubjectId);


    Optional<SurveyStatisticsSurveyExecution> findByOfferedSubjectsIdAndCourseIdAndSessionId(String offeredSubjectsId, String courseId, String sessionId);
    
}

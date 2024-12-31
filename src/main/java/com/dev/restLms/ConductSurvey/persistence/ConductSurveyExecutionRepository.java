package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyExecution;
import com.dev.restLms.entity.SurveyExecution;
import java.util.Optional;


public interface ConductSurveyExecutionRepository extends JpaRepository <SurveyExecution, String> {
    Optional<ConductSurveyExecution> findByCourseIdAndSessionId(String courseId, String sessionId);
    Optional<ConductSurveyExecution> findByCourseIdAndSessionIdAndOfferedSubjectsId(String courseId, String sessionId, String offeredSubjectsId);
}

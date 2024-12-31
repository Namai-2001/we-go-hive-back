package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyExecution;
import com.dev.restLms.entity.SurveyExecution;
import java.util.Optional;


public interface IndividualConductSurveyExecutionRepository extends JpaRepository<SurveyExecution, String> {
    Optional<IndividualConductSurveyExecution> findByOfferedSubjectsIdAndSessionId(String offeredSubjectsId, String sessionId);
}

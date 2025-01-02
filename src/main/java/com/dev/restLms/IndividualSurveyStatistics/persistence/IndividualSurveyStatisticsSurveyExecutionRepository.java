package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyExecution;

import java.util.List;


public interface IndividualSurveyStatisticsSurveyExecutionRepository extends JpaRepository<SurveyExecution, String> {
    List<SurveyExecution> findByOfferedSubjectsIdAndSessionId(String offeredSubjectsId, String sessionId);

    List<SurveyExecution> findBySessionId(String sessionId);

}

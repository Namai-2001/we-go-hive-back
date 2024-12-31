package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyOwnResult;
import com.dev.restLms.entity.SurveyOwnResult;
import java.util.List;


public interface IndividualSurveyStatisticsSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    List<IndividualSurveyStatisticsSurveyOwnResult> findBySurveyExecutionId(String surveyExecutionId);
}

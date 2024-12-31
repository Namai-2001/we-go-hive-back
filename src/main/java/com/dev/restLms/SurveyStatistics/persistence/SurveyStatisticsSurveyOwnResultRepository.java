package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyOwnResult;
import com.dev.restLms.entity.SurveyOwnResult;
import java.util.List;


public interface SurveyStatisticsSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    List<SurveyStatisticsSurveyOwnResult> findBySurveyExecutionId(String surveyExecutionId);

    @RestResource(path = "findBySurveyExecutionIdAndSurveyQuestionId1")
    List<SurveyStatisticsSurveyOwnResult> findBySurveyExecutionIdAndSurveyQuestionId(String surveyExecutionId, String surveyQuestionId);

    @RestResource(path = "findBySurveyExecutionIdAndSurveyQuestionId2")
    Page<SurveyStatisticsSurveyOwnResult> findBySurveyExecutionIdAndSurveyQuestionId(String surveyExecutionId, String surveyQuestionId, Pageable pageable);
}

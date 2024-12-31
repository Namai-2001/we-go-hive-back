package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyOwnAnswer;
import com.dev.restLms.entity.SurveyOwnAnswer;
import java.util.Optional;


public interface SurveyStatisticsSurveyOwnAnswerRepository extends JpaRepository<SurveyOwnAnswer, String> {
    Optional<SurveyStatisticsSurveyOwnAnswer> findBySurveyAnswerIdAndSurveyQuestionId(String surveyAnswerId, String surveyQuestionId);
}

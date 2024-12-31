package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyOwnAnswer;
import com.dev.restLms.entity.SurveyOwnAnswer;
import java.util.Optional;


public interface IndividualSurveyStatisticsSurveyOwnAnswerRepository extends JpaRepository<SurveyOwnAnswer, String> {
    Optional<IndividualSurveyStatisticsSurveyOwnAnswer> findBySurveyAnswerIdAndSurveyQuestionId(String surveyAnswerId, String surveyQuestionId);
}

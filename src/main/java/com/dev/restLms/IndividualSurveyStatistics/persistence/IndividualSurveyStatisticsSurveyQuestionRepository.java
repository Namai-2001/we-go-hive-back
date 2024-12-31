package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;
import java.util.Optional;


public interface IndividualSurveyStatisticsSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Optional<IndividualSurveyStatisticsSurveyQuestion> findBySurveyQuestionId(String surveyQuestionId);
}

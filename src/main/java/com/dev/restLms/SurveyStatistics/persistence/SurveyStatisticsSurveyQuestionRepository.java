package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;
import java.util.Optional;


public interface SurveyStatisticsSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Optional<SurveyStatisticsSurveyQuestion> findBySurveyQuestionId(String surveyQuestionId);
}

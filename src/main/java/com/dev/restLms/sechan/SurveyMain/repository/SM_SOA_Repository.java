package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;
import java.util.Optional;

// import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnAnswer;

public interface SM_SOA_Repository extends JpaRepository<SurveyOwnAnswer, String> {
    List<SurveyOwnAnswer> findBySurveyAnswerIdInAndAnswerDataIsNotNullOrScoreIsNotNull(List<String> surveyAnswerIds);

    Optional<SurveyOwnAnswer> findBySurveyAnswerId(String surveyAnswerId);

    Optional<SurveyOwnAnswer> findBySurveyQuestionId(String surveyQuestionId);
    Optional<SurveyOwnAnswer> findBySurveyQuestionIdAndSurveyAnswerId(String surveyQuestionId, String surveyAnswerId);
}

package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;


public interface ConductSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Page<ConductSurveyQuestion> findBySurveyCategoryAndQuestionInactive(String surveyCategory, String questionInactive, Pageable pageable);
    Page<ConductSurveyQuestion> findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(String questionData, String surveyCategory, String questionInactive, Pageable pageable);
    // Page<ConductSurveyQuestion> findBySurveyCategorAndQuestionInactive(String surveyCategory, String questionInactive, Sort sort, Pageable pageable);
}

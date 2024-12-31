package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;


public interface IndividualConductSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Page<IndividualConductSurveyQuestion> findBySurveyCategoryAndQuestionInactive(String surveyCategory, String questionInactive, Pageable pageable);
    Page<IndividualConductSurveyQuestion> findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(String questionData ,String surveyCategory, String questionInactive, Pageable pageable);
}

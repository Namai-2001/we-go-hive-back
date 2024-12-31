package com.dev.restLms.IndividualItemManagement.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualItemManagement.projection.IndividualItemManagementSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;

public interface IndividualItemManagementSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Page<IndividualItemManagementSurveyQuestion> findBySurveyCategoryAndQuestionInactive(String surveyCategory, String questionInactive, Pageable pageable);

    Page<IndividualItemManagementSurveyQuestion> findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(String questionData, String surveyCategory, String questionInactive, Pageable pageable);

    Optional<IndividualItemManagementSurveyQuestion> findBySurveyQuestionId(String surveyQuestionId);
}

package com.dev.restLms.ItemManagement.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ItemManagement.projection.ItemManagementSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;
import java.util.Optional;



public interface ItemManagementSurveyQuestionRepository extends JpaRepository<SurveyQuestion, String> {
    Page<ItemManagementSurveyQuestion> findBySurveyCategoryAndQuestionInactive(String surveyCategory, String questionInactive, Pageable pageable);

    Page<ItemManagementSurveyQuestion> findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(String questionData, String surveyCategory, String questionInactive, Pageable pageable);

    Optional<ItemManagementSurveyQuestion> findBySurveyQuestionId(String surveyQuestionId);

}

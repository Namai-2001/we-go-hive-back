package com.dev.restLms.IndividualItemManagement.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualItemManagement.projection.IndividualItemManagementSurveyOwnResult;
import com.dev.restLms.entity.SurveyOwnResult;

public interface IndividualItemManagementSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    List<IndividualItemManagementSurveyOwnResult> findBySurveyQuestionId(String surveyQuestionId);
}

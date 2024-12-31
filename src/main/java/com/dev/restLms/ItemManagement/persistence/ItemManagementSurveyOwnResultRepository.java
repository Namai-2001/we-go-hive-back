package com.dev.restLms.ItemManagement.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ItemManagement.projection.ItemManagementSurveyOwnResult;
import com.dev.restLms.entity.SurveyOwnResult;

import java.util.List;


public interface ItemManagementSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    List<ItemManagementSurveyOwnResult> findBySurveyQuestionId(String surveyQuestionId);
}

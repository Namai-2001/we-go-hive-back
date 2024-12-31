package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyQuestion;
import com.dev.restLms.sechan.SurveyMain.projection.SM_SQ_Projection;

public interface SM_SQ_Repository extends JpaRepository<SurveyQuestion, String> {
    List<SM_SQ_Projection> findBySurveyCategory(String surveyCategory);
}

package com.dev.restLms.sechan.SurveyMain.repository;

// import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnAnswer;

public interface SM_SOA_Repository extends JpaRepository<SurveyOwnAnswer, String>{
    // List<SurveyOwnAnswer> findBySurveyQuestionId(String surveyQuestionId);
}

package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnAnswer;

public interface ConductSurveyOwnAnswerRepository extends JpaRepository<SurveyOwnAnswer, String> {
    
}

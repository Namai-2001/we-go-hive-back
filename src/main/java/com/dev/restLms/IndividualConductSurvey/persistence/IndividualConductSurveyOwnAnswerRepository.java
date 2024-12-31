package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnAnswer;

public interface IndividualConductSurveyOwnAnswerRepository extends JpaRepository<SurveyOwnAnswer, String> {
    
}

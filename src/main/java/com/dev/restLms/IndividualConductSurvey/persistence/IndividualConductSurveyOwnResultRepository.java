package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnResult;

public interface IndividualConductSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    
}

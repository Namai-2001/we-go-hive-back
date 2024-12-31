package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnResult;

public interface ConductSurveyOwnResultRepository extends JpaRepository<SurveyOwnResult, String> {
    
}

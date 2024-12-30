package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyOwnResult;

public interface CCP_SOR_Repository extends JpaRepository<SurveyOwnResult, String> {
    List<SurveyOwnResult> findBySurveyExecutionIdAndSessionId(String surveyExecutionId, String sessionId);
}

package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyQuestion;

public interface SM_SQ2_Repository extends JpaRepository<SurveyQuestion, String> {
    // 특정 설문 카테고리에 해당하는 질문 검색
    // List<SurveyQuestion> findBySurveyCategory(String surveyCategory);

    // SurveyExecution ID를 기준으로 질문 검색 (Custom Query로 구현 필요 시 추가)
    // List<SurveyQuestion> findBySurveyExecutionId(String surveyExecutionId);
    List<SurveyQuestion> findBySurveyQuestionIdIn(List<String> surveyQuestionIds);
}

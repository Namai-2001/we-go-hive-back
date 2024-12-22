package com.dev.restLms.sechan.SurveyMain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SM_Survey_DTO {
    private String surveyQuestionId;   // 질문 ID
    private String score;              // 5지선다 점수 (1~5)
    private String answerData;         // 서술형 답변
    private String surveyExecutionId;  // 만족도 조사 실행 ID
    private String sessionId;          // 사용자 ID           
}

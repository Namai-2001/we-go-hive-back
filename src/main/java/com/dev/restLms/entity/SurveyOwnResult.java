package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SurveyOwnResult {
    @Id
    private String increaseId; //기본키
    private String surveyExecutionId; // 특정 과목 or 과정에 대한 만족도 조사 실시 키 값(SurveyExecution)
    private String sessionId; // 회원가입 시에 발급되는 고유 키값
    private String surveyQuestionId; // 특정 문항에 대한 고유 키 값
    private String surveyAnswerId; // 특정 문항에 대한 답변의 고유 키 값(SurveyOwnAnswer)

    @PrePersist
    public void generateUUID() {
        if (increaseId == null) {
            increaseId = UUID.randomUUID().toString();
        }
    }
}

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

public class SurveyOwnAnswer {
    @Id
    private String surveyAnswerId; // 특정 문항에 대한 고유 키 값
    private String surveyQuestionId; // 문항 내용
    private String answerData; // T, F로 5지선다, 서술형으로 구분
    private String score; // 과목 or 과정을 식별

    @PrePersist
    public void generateUUID() {
        if (surveyAnswerId == null) {
            surveyAnswerId = UUID.randomUUID().toString();
        }
    }
}

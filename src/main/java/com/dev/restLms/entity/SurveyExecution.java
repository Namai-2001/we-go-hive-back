package com.dev.restLms.entity;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyExecution {
    @Id
    private String surveyExecutionId; // 특정 과목 * 과정에 대한 만족도 조사 실시 키 값
    private String offeredSubjectsId; // 개설 과목 코드의 고유 키 값(OfferedSubjects)
    private String courseId; // 과정 생성 시 발급되는 고유 키 값(Course)
    private String sessionId; // 책임자 아이디 ID

    @PrePersist
    public void generateUUID() {
        if (surveyExecutionId == null) {
            surveyExecutionId = UUID.randomUUID().toString();
        }
    }
}

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyExecution {

    @Id
    private String surveyExecutionId;

    private String offeredSubjectsId;
    private String courseId;
    private String sessionId;

    @PrePersist
  public void generateUUID() {
      if (surveyExecutionId == null) {
        surveyExecutionId = UUID.randomUUID().toString();
      }
  }
    
}

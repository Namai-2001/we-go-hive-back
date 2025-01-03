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
public class SurveyQuestion {
    @Id
    private String surveyQuestionId;

    private String questionData;
    private String answerCategory;
    private String surveyCategory;
    private String questionInactive;

    @PrePersist
  public void generateUUID() {
      if (surveyQuestionId == null) {
        surveyQuestionId = UUID.randomUUID().toString();
      }
  }
    
}

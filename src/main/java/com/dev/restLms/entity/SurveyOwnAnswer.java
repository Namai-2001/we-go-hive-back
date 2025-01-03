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
public class SurveyOwnAnswer {

    @Id
    private String surveyAnswerId;
    private String surveyQuestionId;
    private String answerData;
    private String score;

    @PrePersist
  public void generateUUID() {
      if (surveyAnswerId == null) {
        surveyAnswerId = UUID.randomUUID().toString();
      }
  }
    
}

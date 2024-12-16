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
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOwnAssignmentEvaluation {
  @Id
  private String submissionId;
  private String uoaeSessionId;
  private String assignmentId;
  private String teacherSessionId;
  private String score;
  private String fileNo;
  private String isSubmit;

    @PrePersist
    public void generateUUID() {
        if (submissionId == null) {
          submissionId = UUID.randomUUID().toString();
        }
    }
}

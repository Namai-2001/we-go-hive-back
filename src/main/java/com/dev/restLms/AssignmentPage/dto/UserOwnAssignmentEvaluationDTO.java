package com.dev.restLms.AssignmentPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOwnAssignmentEvaluationDTO {
  private String uoaeSessionId;
  private String assignmentId;
  private String subjectName;
  private String assignmentName;
}
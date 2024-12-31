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
  // 과제를 특정 짓기 위해
  private String assignmentId;
  // 파일 시스템에 저장되는 경로를 설정하기 위해
  private String subjectName;
  private String assignmentName;
}
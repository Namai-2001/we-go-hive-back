package com.dev.restLms.AssignmentPage.projection;

// 사용자별 과제 평가 목록에서 제출 고유 코드 & 강사 아이디를 추출하기 위함
public interface AssignmentPageUserOwnAssignmentEvaluationProjection {
  String getAssignmentId();
  String getTeacherSessionId();
  String getScore();
  String getFileNo();
  String getIsSubmit();
}

package com.dev.restLms.sechan.teacherAssignment.projection;

public interface TA_A_Projection {
    String getAssignmentId();
    String getAssignmentTitle(); // 과제명
    String getDeadline(); // 제출 기한
    String getNoticeNo(); // 게시 날짜
    String getCutline(); // 기준 점수
    String getAssignmentContent(); // 과제 내용용

}

package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;

public interface SID_UOA_Repository extends JpaRepository<UserOwnAssignment, String> {
    // 특정 사용자가 이미 신청한 과목 조회
    List<UserOwnAssignment> findByUserSessionId(String userSessionId);

    // 특정 사용자가 특정 개설 과목을 신청했는지 확인
    boolean existsByUserSessionIdAndOfferedSubjectsId(String userSessionId, String offeredSubjectsId);
}
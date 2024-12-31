package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;

public interface UOA_Repository extends JpaRepository<UserOwnAssignment, String> {
    //사용자 세션 ID로 개설 과목 ID 목록 조회
    List<UserOwnAssignment> findByUserSessionId(String userSessionId);

    //subjectAcceptCategory 'T'인 과목 조회 (개별과목이기 때문)
    List<UserOwnAssignment> findByUserSessionIdAndSubjectAcceptCategory(String userSessionId, String subjectAcceptCategory);
}

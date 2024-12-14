package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_U_Projection;

public interface SID_U_Repository extends JpaRepository<User, String> {
    // sessionId를 기준으로 강사 이름 조회
    SID_U_Projection findBySessionId(String sessionId);
}

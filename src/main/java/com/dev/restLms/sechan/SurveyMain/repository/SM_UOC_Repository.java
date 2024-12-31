package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOC_Projection;

public interface SM_UOC_Repository extends JpaRepository<UserOwnCourse, String> {
    // 세션 아이디와 courseApproval에 따라 과정 가져오기
    List<SM_UOC_Projection> findBySessionId(String sessionId);
}

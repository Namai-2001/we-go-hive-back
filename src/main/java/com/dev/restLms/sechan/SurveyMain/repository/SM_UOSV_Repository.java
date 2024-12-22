package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOSV_Projection;

public interface SM_UOSV_Repository extends JpaRepository<UserOwnSubjectVideo, String> {
    List<SM_UOSV_Projection> findByUosvSessionId(String uosvSessionId);
    // List<UserOwnSubjectVideo> findByUosvOfferedSubjectsIdAndUosvSessionId(String offeredSubjectsId, String sessionId);
}

package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.sechan.courseCompletePage.projection.CCP_UOSV_Projection;

public interface CCP_UOSV_Repository extends JpaRepository<UserOwnSubjectVideo, String>{
    List<CCP_UOSV_Projection> findByUosvOfferedSubjectsIdAndUosvSessionId(String offeredSubjectsId, String sessionId);
}


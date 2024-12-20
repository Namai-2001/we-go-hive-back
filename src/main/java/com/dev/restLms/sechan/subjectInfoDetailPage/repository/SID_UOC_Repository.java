package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_CourseCheck_Projection;

public interface SID_UOC_Repository extends JpaRepository<UserOwnCourse, String> {
    List<SID_CourseCheck_Projection> findBySessionId(String sessionId);
}
    

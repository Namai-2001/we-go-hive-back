package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnCourse;

public interface CCP_UOC2_Repository extends JpaRepository<UserOwnCourse, String> {
    List<UserOwnCourse> findBySessionId(String sessionId);
}

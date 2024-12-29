package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnCourse;

public interface CCP_UOC_Repository extends JpaRepository<UserOwnCourse, String> {
    Optional<UserOwnCourse> findByCourseIdAndSessionId(String courseId, String sessionId);
}

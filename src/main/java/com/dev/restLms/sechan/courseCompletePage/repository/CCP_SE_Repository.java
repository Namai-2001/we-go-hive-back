package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SurveyExecution;

public interface CCP_SE_Repository extends JpaRepository<SurveyExecution, String> {
    List<SurveyExecution> findByCourseId(String courseId);
}

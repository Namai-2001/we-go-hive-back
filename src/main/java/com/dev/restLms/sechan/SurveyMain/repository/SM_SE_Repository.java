package com.dev.restLms.sechan.SurveyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.dev.restLms.entity.SurveyExecution;

public interface SM_SE_Repository extends JpaRepository<SurveyExecution, String> {
    List<SurveyExecution> findByCourseId(String courseId);
    Optional<SurveyExecution> findByOfferedSubjectsId(String offeredSubjectsId);
}
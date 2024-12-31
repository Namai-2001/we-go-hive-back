package com.dev.restLms.sechan.SurveyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.sechan.SurveyMain.projection.SM_S_Projection;

public interface SM_S_Repository extends JpaRepository<Subject, String> {
    SM_S_Projection findBySubjectId(String subjectId);
}

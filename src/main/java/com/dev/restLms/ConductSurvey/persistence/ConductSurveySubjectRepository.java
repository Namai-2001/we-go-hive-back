package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveySubject;
import com.dev.restLms.entity.Subject;
import java.util.Optional;


public interface ConductSurveySubjectRepository extends JpaRepository<Subject, String> {
    Optional<ConductSurveySubject> findBySubjectId(String subjectId);
}

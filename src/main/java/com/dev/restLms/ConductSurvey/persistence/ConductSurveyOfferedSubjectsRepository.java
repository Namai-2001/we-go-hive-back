package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.Optional;


public interface ConductSurveyOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<ConductSurveyOfferedSubjects> findByCourseIdAndSubjectIdAndOfficerSessionId(String courseId, String subjectId, String officerSessionId);
}

package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.Optional;


public interface IndividualConductSurveyOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<IndividualConductSurveyOfferedSubjects> findByCourseIdAndSubjectIdAndOfficerSessionId(String courseId, String subjectId, String officerSessionId);
}

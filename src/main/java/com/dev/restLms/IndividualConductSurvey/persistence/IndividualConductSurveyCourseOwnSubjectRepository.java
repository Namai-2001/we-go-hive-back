package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;
import java.util.Optional;


public interface IndividualConductSurveyCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    List<IndividualConductSurveyCourseOwnSubject> findByCourseIdAndOfficerSessionIdAndSubjectApproval(String courseId, String officerSessionId, String subjectApproval);

    Optional<IndividualConductSurveyCourseOwnSubject> findBySubjectIdAndOfficerSessionIdAndSubjectApproval(String subjectId, String officerSessionId, String subjectApproval);
}

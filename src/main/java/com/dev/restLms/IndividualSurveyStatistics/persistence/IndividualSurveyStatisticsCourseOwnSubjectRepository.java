package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.Optional;
import java.util.List;



public interface IndividualSurveyStatisticsCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    Optional<IndividualSurveyStatisticsCourseOwnSubject> findByCourseIdAndSubjectIdAndOfficerSessionIdAndSubjectApproval(String courseId, String subjectId, String officerSessionId, String subjectApproval);

    Optional<IndividualSurveyStatisticsCourseOwnSubject> findBySubjectId(String subjectId);
}

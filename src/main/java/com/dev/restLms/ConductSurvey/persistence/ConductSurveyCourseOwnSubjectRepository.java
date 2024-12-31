package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;


public interface ConductSurveyCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    List<ConductSurveyCourseOwnSubject> findByCourseIdAndOfficerSessionId(String courseId, String officer);
}

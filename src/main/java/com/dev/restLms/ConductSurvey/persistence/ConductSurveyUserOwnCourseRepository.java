package com.dev.restLms.ConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ConductSurvey.projection.ConductSurveyUserOwnCourse;
import com.dev.restLms.entity.UserOwnCourse;
import java.util.List;


public interface ConductSurveyUserOwnCourseRepository extends JpaRepository<UserOwnCourse, String>{

    List<ConductSurveyUserOwnCourse> findByCourseIdAndOfficerSessionId(String courseId, String OfficerSessionId);

}
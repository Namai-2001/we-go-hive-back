package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;


public interface ModifyCourseOwnSubjectRepository extends JpaRepository <CourseOwnSubject, String> {
    List<ModifyCourseOwnSubject> findByCourseIdAndOfficerSessionId(String courseId, String officerSessionId);
    List<ModifyCourseOwnSubject> findByCourseIdAndSubjectApproval(String courseId, String subjectApproval);
}

package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;


public interface DeleteCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    List<DeleteCourseOwnSubject> findByCourseIdAndOfficerSessionId(String courseId, String officerSessionId);
    void deleteByCourseId(String courseId);
}

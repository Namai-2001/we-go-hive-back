package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.CreateCourse.projection.CreateCourseOwnSubjuct;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;
import java.util.Optional;


public interface CreateCourseOwnSubjuctRepository extends JpaRepository<CourseOwnSubject, String> {
    List<CreateCourseOwnSubjuct> findByCourseIdAndSubjectApproval(String courseId , String subjectApproval);

    Optional<CreateCourseOwnSubjuct> findBySubjectIdAndSubjectApproval(String subjectId, String subjectApproval);

}

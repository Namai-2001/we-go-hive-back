package com.dev.restLms.HomePage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.HomePage.projection.HomeCourseOwnSubjectProjection;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;
import java.util.Optional;


public interface HomeCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    Optional<List<HomeCourseOwnSubjectProjection>> findByCourseIdAndSubjectApproval(String courseId, String subjectApproval);
}
package com.dev.restLms.ProcessList;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.Course;


@Repository
public interface ProcessListCourseRepository extends JpaRepository<Course,String> {
    Optional<ProcessListCourse> findBycourseId(String courseId);
    Page<Course> findByCourseIdNot(String excludedCourseId, Pageable pageable);
    Page<Course> findByCourseIdNotAndCourseTitleContaining(String excludedCourseId, String courseTitle, Pageable pageable);
}

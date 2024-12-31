package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.CreateCourse.projection.CreateCourse;
import com.dev.restLms.entity.Course;
import java.util.Optional;


public interface CreateCourseRepository extends JpaRepository<Course, String> {
    Optional<CreateCourse> findByCourseId(String courseId);
}

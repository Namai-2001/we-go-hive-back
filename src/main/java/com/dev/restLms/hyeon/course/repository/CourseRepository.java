package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
	Course findBycourseId (String courseId);
}

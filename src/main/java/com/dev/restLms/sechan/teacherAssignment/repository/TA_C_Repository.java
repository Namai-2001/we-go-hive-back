package com.dev.restLms.sechan.teacherAssignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;

public interface TA_C_Repository extends JpaRepository<Course, String> {
    Course findByCourseId(String courseId);
}

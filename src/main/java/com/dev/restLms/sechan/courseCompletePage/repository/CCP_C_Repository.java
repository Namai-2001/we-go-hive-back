package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;

public interface CCP_C_Repository extends JpaRepository<Course, String> {
    Optional<Course> findById(String courseId);
}

package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;

public interface TV_C_Repository extends JpaRepository<Course, String> {
    Optional<Course> findByCourseId(String courseId);
}

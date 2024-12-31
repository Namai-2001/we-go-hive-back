package com.dev.restLms.sechan.subjectRegister.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;

public interface SR_C_Repository extends JpaRepository<Course, String> {

    // courseId로 Course 정보 조회
    Optional<Course> findByCourseId(String courseId);
}
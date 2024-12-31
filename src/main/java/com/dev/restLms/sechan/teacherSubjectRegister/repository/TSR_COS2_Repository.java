package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.CourseOwnSubject;

public interface TSR_COS2_Repository extends JpaRepository<CourseOwnSubject, String> {
    List<CourseOwnSubject> findBySubjectId(String subjectId);
}

package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.CourseOwnSubject;

public interface CCP_COS_Repository extends JpaRepository<CourseOwnSubject, String>{
    List<CourseOwnSubject> findByCourseId(String courseId);
}

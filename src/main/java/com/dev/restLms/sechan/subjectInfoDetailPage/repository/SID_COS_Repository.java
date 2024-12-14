package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.CourseOwnSubject;
import java.util.List;


public interface SID_COS_Repository extends JpaRepository<CourseOwnSubject, String> {
    List<CourseOwnSubject> findByCourseId(String courseId);
}

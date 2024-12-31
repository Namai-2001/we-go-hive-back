package com.dev.restLms.hyeon.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.CourseOwnSubject;

@Repository
public interface CourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    List<CourseOwnSubject> findByCourseId(String courseId); // Spring Data JPA 메서드 쿼리
}
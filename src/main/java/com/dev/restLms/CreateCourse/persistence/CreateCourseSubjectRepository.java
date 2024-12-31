package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.CreateCourse.projection.CreateCourseSubject;
import com.dev.restLms.entity.Subject;

import java.util.List;
import java.util.Optional;



public interface CreateCourseSubjectRepository extends JpaRepository<Subject, String> {
    // Page<CreateCourseSubject> findBySubjectNameContaining(String subjectName, Pageable pageable);
    Page<CreateCourseSubject> findBySubjectIdInAndSubjectNameContaining(List<String> subjectIds, String subjectName, Pageable pageable);
    Optional<CreateCourseSubject> findBySubjectId(String subjectId);
}

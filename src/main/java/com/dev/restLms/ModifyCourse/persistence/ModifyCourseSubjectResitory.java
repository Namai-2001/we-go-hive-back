package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseSubject;
import com.dev.restLms.entity.Subject;
import java.util.Optional;
import java.util.List;



public interface ModifyCourseSubjectResitory extends JpaRepository<Subject, String> {
    Optional<ModifyCourseSubject> findBySubjectId(String subjectId);
    Page<ModifyCourseSubject> findBySubjectIdInAndSubjectNameContaining(List<String> subjectIds, String subjectName, Pageable pageable);
}

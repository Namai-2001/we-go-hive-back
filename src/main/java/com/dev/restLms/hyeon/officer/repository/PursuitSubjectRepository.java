package com.dev.restLms.hyeon.officer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.hyeon.officer.projection.PursuitSubject;

@Repository
public interface PursuitSubjectRepository extends JpaRepository<Subject, String> {
    List<PursuitSubject> findBySubjectIdInAndTeacherSessionId(List<String> subjectId, String teacherSessionId);
    Optional<PursuitSubject> findBySubjectId(String subjectId);
}
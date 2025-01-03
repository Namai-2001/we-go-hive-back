package com.dev.restLms.hyeon.officer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Subject;

@Repository
public interface TeacherAssignmentSubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectId(String subjectId);
    Optional<Subject> findBySubjectIdAndTeacherSessionId(String subjectId, String teacherSessionId);
}
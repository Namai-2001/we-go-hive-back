package com.dev.restLms.sechan.teacherAssignment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;

public interface TA_S_Repository extends JpaRepository<Subject, String>{
    Optional<Subject> findBySubjectId(String subjectId);
}

package com.dev.restLms.sechan.subjectRegister.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;

public interface SR_S_Repository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectId(String subjectId);
}

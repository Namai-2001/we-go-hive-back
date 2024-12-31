package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.sechan.teacherVideo.projection.TV_S_Projection;

public interface TV_S_Repository extends JpaRepository<Subject, String> {
    Optional<TV_S_Projection> findBySubjectId(String subjectId);
}

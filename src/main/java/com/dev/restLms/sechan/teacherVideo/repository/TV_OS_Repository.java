package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface TV_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    List<OfferedSubjects> findByTeacherSessionId(String teacherSessionId);
}

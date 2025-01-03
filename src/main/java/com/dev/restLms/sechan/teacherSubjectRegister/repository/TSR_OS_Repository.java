package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface TSR_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    List<OfferedSubjects> findBySubjectId(String subjectId);
}

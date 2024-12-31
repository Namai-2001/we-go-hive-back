package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;

public interface TSR_S_Repository extends JpaRepository<Subject, String> {
    List<Subject> findByTeacherSessionId(String teacherSessionId);

}

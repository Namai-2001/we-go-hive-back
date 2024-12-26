package com.dev.restLms.sechan.subjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.sechan.subjectRegister.projection.SR_S_Projection;

public interface SR_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    List<SR_S_Projection> findByTeacherSessionId(String teacherSessionId);
}

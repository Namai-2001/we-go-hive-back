package com.dev.restLms.sechan.teacherAssignment.repository;

import com.dev.restLms.entity.OfferedSubjects;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TA_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    // 강사사가 개설한 과목 목록 조회
    List<OfferedSubjects> findByTeacherSessionId(String teacherSessionId);
}
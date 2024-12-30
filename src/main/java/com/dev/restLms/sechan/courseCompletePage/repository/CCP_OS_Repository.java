package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface CCP_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    List<OfferedSubjects> findByCourseId(String courseId);
}

package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface SID_OS2_Repository extends JpaRepository<OfferedSubjects, String> {
    // 특정 subjectId에 해당하는 개설 과목 조회
    OfferedSubjects findBySubjectId(String subjectId);

    // 특정 courseId에 속하는 모든 과목 조회
    List<OfferedSubjects> findByCourseId(String courseId);
}

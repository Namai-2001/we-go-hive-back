package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.CourseOwnSubject;

public interface TSR_COS_Repository extends JpaRepository<CourseOwnSubject, String> {
    Optional<CourseOwnSubject> findBySubjectId(String subjectId);

    // 삭제 메서드 추가
    void deleteBySubjectId(String subjectId);
}

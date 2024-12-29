package com.dev.restLms.sechan.teacherSubjectRegister.repository;

// import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.CourseOwnSubject;

public interface TSR_COS_Repository extends JpaRepository<CourseOwnSubject, String> {
    Optional<CourseOwnSubject> findBySubjectIdAndCourseId(String subjectId, String courseId);

    // 삭제 메서드 추가
    void deleteBySubjectId(String subjectId);

    // 특정 과목 ID에 관련된 모든 데이터 가져오기
    // List<CourseOwnSubject> findBySubjectId(String subjectId);
}

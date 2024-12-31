package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.S_Projection;
// import java.util.List;
import java.util.Optional;

public interface S_Repository extends JpaRepository<Subject, String> {
    // 과목 ID로 과목 정보 조회
    //Optional<S_Projection> findBySubjectId(String subjectId);
    Optional<Subject> findBySubjectId(String subjectId);
}
